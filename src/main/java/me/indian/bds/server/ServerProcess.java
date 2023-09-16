package me.indian.bds.server;

import me.indian.bds.BDSAutoEnable;
import me.indian.bds.config.Config;
import me.indian.bds.discord.DiscordIntegration;
import me.indian.bds.exception.BadThreadException;
import me.indian.bds.logger.LogState;
import me.indian.bds.logger.Logger;
import me.indian.bds.manager.player.PlayerManager;
import me.indian.bds.server.properties.Difficulty;
import me.indian.bds.util.MessageUtil;
import me.indian.bds.util.StatusUtil;
import me.indian.bds.util.ThreadUtil;
import me.indian.bds.watchdog.WatchDog;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerProcess {

    private final BDSAutoEnable bdsAutoEnable;
    private final Logger logger;
    private final Config config;
    private final DiscordIntegration discord;
    private final PlayerManager playerManager;
    private final ExecutorService processService;
    private final String prefix;
    private String finalFilePath;
    private ProcessBuilder processBuilder;
    private Process process;
    private PrintWriter writer;
    private String lastLine;
    private long startTime;
    private WatchDog watchDog;
    private boolean canRun;

    public ServerProcess(final BDSAutoEnable bdsAutoEnable) {
        this.bdsAutoEnable = bdsAutoEnable;
        this.logger = this.bdsAutoEnable.getLogger();
        this.config = this.bdsAutoEnable.getConfig();
        this.discord = this.bdsAutoEnable.getDiscord();
        this.playerManager = this.bdsAutoEnable.getPlayerManager();
        this.processService = Executors.newScheduledThreadPool(ThreadUtil.getThreadsCount(), new ThreadUtil("Server process"));
        this.prefix = "&b[&3ServerProcess&b] ";
        this.canRun = true;
    }

    public void initWatchDog(final WatchDog watchDog) {
        this.watchDog = watchDog;
    }

    public boolean isProcessRunning() {
        try {
            String command = "";
            switch (this.config.getSystem()) {
                case LINUX -> command = "pgrep -f " + this.config.getFileName();
                case WINDOWS -> command = "tasklist /NH /FI \"IMAGENAME eq " + this.config.getFileName() + "\"";
                default -> {
                    this.logger.critical("Musisz podać odpowiedni system");
                    System.exit(0);
                }
            }

            final Process checkProcessIsRunning = Runtime.getRuntime().exec(command);
            try (final BufferedReader reader = new BufferedReader(new InputStreamReader(checkProcessIsRunning.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.isEmpty() && !line.equalsIgnoreCase("INFO: No tasks are running which match the specified criteria.")) {
                        return true;
                    }
                }
                checkProcessIsRunning.waitFor();
            }
        } catch (final IOException | InterruptedException exception) {
            this.logger.critical("Nie można sprawdzić czy proces jest aktywny", exception);
            System.exit(0);
        }
        return false;
    }

    public void startProcess() {
        if (!this.canRun) {
            this.logger.debug("Nie można uruchomić procesu ponieważ&b canRun&r jest ustawione na:&b " + false);
            return;
        }
        this.finalFilePath = this.config.getFilesPath() + File.separator + this.config.getFileName();
        this.processService.execute(() -> {
            if (this.isProcessRunning()) {
                this.logger.info("Proces " + this.config.getFileName() + " jest już uruchomiony.");
                System.exit(0);
            } else {
                this.logger.info("Proces " + this.config.getFileName() + " nie jest uruchomiony. Uruchamianie...");
                try {
                    switch (this.config.getSystem()) {
                        case LINUX -> {
                            if (this.config.isWine()) {
                                this.processBuilder = new ProcessBuilder("wine", this.finalFilePath);
                            } else {
                                this.processBuilder = new ProcessBuilder("./" + this.config.getFileName());
                                this.processBuilder.environment().put("LD_LIBRARY_PATH", ".");
                                this.processBuilder.directory(new File(this.config.getFilesPath()));
                            }
                        }
                        case WINDOWS -> this.processBuilder = new ProcessBuilder(this.finalFilePath);
                        default -> {
                            this.logger.critical("Musisz podać odpowiedni system");
                            System.exit(0);
                        }
                    }
                    this.process = this.processBuilder.start();
                    this.startTime = System.currentTimeMillis();
                    this.logger.info("Uruchomiono proces ");
                    this.discord.sendProcessEnabledMessage();

                    this.logger.debug("&bPID&r procesu servera to&1 " + this.process.pid());

                    final Thread output = new ThreadUtil("Console-Output").newThread(this::readConsoleOutput);
                    final Thread input = new ThreadUtil("Console-Input").newThread(this::writeConsoleInput);

                    output.start();
                    input.start();

                    this.logger.alert("Proces zakończony z kodem: " + this.process.waitFor());
                    this.playerManager.clearPlayers();
                    this.playerManager.getStatsManager().saveAllData();
                    output.interrupt();
                    input.interrupt();
                    this.discord.sendDisabledMessage();
                    this.startProcess();
                } catch (final Exception exception) {
                    this.logger.critical("Nie można uruchomić procesu", exception);
                    ;
                    System.exit(0);
                }
            }
        });
    }

    private void readConsoleOutput() {
        try (final Scanner consoleOutput = new Scanner(this.process.getInputStream())) {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    if (!consoleOutput.hasNext()) continue;
                    final String line = consoleOutput.nextLine();
                    if (line.isEmpty()) continue;
                    if (!this.containsNotAllowedToFileLog(line)) {
                        this.logger.instantLogToFile(line);
                    }
                    if (!this.containsNotAllowedToConsoleLog(line)) {
                        System.out.println(line);
                        this.lastLine = line;
                        this.playerManager.initFromLog(line);
                    }
                    if (!this.containsNotAllowedToDiscordConsoleLog(line)) {
                        this.discord.writeConsole(line);
                    }
                }
            } catch (final Exception exception) {
                this.logger.critical("Czytanie konsoli uległo awarii , powoduje to wyłączenie aplikacji ", exception);
                this.discord.sendMessage("<owner> Czytanie konsoli uległo awarii , powoduje to wyłączenie aplikacji \n```" + exception + "```");
                System.exit(0);
            }
        }
    }

    private void writeConsoleInput() {
        try (final Scanner consoleInput = new Scanner(System.in)) {
            try {
                this.writer = new PrintWriter(this.process.getOutputStream());
                while (!Thread.currentThread().isInterrupted()) {
                    if (!consoleInput.hasNext()) continue;
                    final String input = consoleInput.nextLine();

                    if (input.startsWith("say")) this.discord.sendPlayerMessage("say", input.substring(3));

                    switch (input.toLowerCase()) {
                        case "stop" -> {
                            this.tellrawToAllAndLogger(this.prefix, "&4Zamykanie servera...", LogState.ALERT);
                            this.kickAllPlayers(this.prefix + "&cKtoś wykonał &astop &c w konsoli servera , \n co skutkuje  restartem");
                            if (!Thread.currentThread().isInterrupted()) ThreadUtil.sleep(2);
                            this.sendToConsole("stop");
                        }
                        case "version" -> {
                            this.tellrawToAllAndLogger(this.prefix, "&aWersja minecraft:&b " + this.config.getVersionManagerConfig().getVersion(), LogState.INFO);
                            this.tellrawToAllAndLogger(this.prefix, "&aWersja BDS-Auto-Enable:&b " + this.bdsAutoEnable.getProjectVersion(), LogState.INFO);
                        }
                        case "backup" -> this.watchDog.getBackupModule().forceBackup();
                        case "test" -> {
                            for (final Map.Entry<Thread, StackTraceElement[]> entry : Thread.getAllStackTraces().entrySet()) {
                                final Thread thread = entry.getKey();

                                System.out.println("Thread ID: " + thread.getId());
                                System.out.println("Thread Name: " + thread.getName());
                                System.out.println("Thread State: " + thread.getState());
                                System.out.println("Thread Is Active: " + thread.isAlive());
                                System.out.println("Thread Is Daemon: " + thread.isDaemon());
                                System.out.println("Thread Is Interrupted: " + thread.isInterrupted());

                                System.out.println("-----------------------------");
                            }
                        }
                        case "stats" -> {
                            for (final String s : StatusUtil.getStatus(false)) {
                                this.logger.info(s);
                            }
                        }
                        case "playtime" -> {
                            for (final String s : StatusUtil.getTopPlayTime(false, 20)) {
                                this.logger.info(s);
                            }
                        }
                        case "deaths" -> {
                            for (final String s : StatusUtil.getTopDeaths(false, 20)) {
                                this.logger.info(s);
                            }
                        }
                        default -> this.sendToConsole(input);
                    }
                }
            } catch (final Exception exception) {
                this.logger.critical("Czytanie konsoli uległo awarii , powoduje to wyłączenie aplikacji ", exception);
                this.discord.sendMessage("<owner> Wypisywanie konsoli uległo awarii , powoduje to wyłączenie aplikacji   \n```" + exception + "```");
                System.exit(0);
            }
        }
    }

    public void instantShutdown() {
        this.logger.alert("Wyłączanie...");
        this.discord.sendDisablingMessage();
        this.setCanRun(false);
        if (this.processService != null && !this.processService.isTerminated()) {
            this.logger.info("Zatrzymywanie wątków procesu servera");
            try {
                this.processService.shutdown();
                ThreadUtil.sleep(2);
                this.logger.info("Zatrzymano wątki procesu servera");
            } catch (final Exception exception) {
                this.logger.error("Nie udało się zatrzymać wątków procesu servera", exception);
            }
        }

        this.kickAllPlayers(this.prefix + "&cServer jest zamykany");
        ThreadUtil.sleep(3);
        this.bdsAutoEnable.getPlayerManager().getStatsManager().saveAllData();

        if (this.process != null && this.process.isAlive()) this.watchDog.saveAndResume();

        this.sendToConsole("stop");

        if (this.writer != null) {
            this.logger.info("Zatrzymywanie writera");
            try {
                this.writer.close();
                this.logger.info("Zatrzymano writer");
            } catch (final Exception exception) {
                this.logger.error("Błąd podczas zamykania writera", exception);
            }
        }

        if (this.process != null && this.process.isAlive()) {
            this.logger.info("Niszczenie procesu servera");
            try {
                this.process.destroy();
                this.logger.info("Zniszczono proces servera");
                this.discord.sendDestroyedMessage();
            } catch (final Exception exception) {
                this.logger.error("Nie udało się zniszczyć procesu servera", exception);
            }
        }

        this.logger.info("Zapisywanie configu...");
        try {
            this.config.load();
            this.config.save();
            this.logger.info("Zapisano config");
        } catch (final Exception exception) {
            this.logger.critical("Nie można zapisać configu", exception);
        }
        this.discord.disableBot();
    }

    public void sendToConsole(final String command) {
        if (this.writer == null) {
            this.logger.critical("Nie udało wysłać się wiadomości do konsoli ponieważ, Writer jest&c nullem&r!");
            return;
        }
        if (!this.isEnabled()) {
            this.logger.debug("Nie udało wysłać się wiadomości do konsoli ponieważ, Process jest&b nullem&r albo nie jest aktywny");
            return;
        }

        this.writer.println(command);
        this.writer.flush();
    }

    public String commandAndResponse(final String command) {
        if (ThreadUtil.isImportantThread()) {
            throw new BadThreadException("Nie możesz wykonać tego na tym wątku!");
        }
        this.sendToConsole(command);
        ThreadUtil.sleep(1);
        return this.lastLine == null ? "null" : this.lastLine;
    }

    public void changeSetting(final ServerSetting serverSetting, final Object option) {
        switch (serverSetting) {
            case difficulty -> {
                if (option instanceof final Difficulty difficulty) {
                    this.sendToConsole("changesetting " + serverSetting.getName() + " " + difficulty.getDifficultyName());
                    this.bdsAutoEnable.getServerProperties().setDifficulty(difficulty);
                    this.logger.info("Zmieniono&b difficulty&r na:&1 " + difficulty);
                } else {
                    this.logger.error("&b" + serverSetting.getName() + "&c przyjmuje wartości z enum&b Difficulty ");
                }
            }
            case allowCheats -> {
                if (option instanceof final Boolean opt) {
                    this.sendToConsole("changesetting " + serverSetting.getName() + " " + opt);
                    this.bdsAutoEnable.getServerProperties().setAllowCheats(opt);
                    this.logger.info("Zmieniono&b difficulty&r na:&1 " + opt);
                }
            }
            default -> this.logger.error("Nie znany typ:&1 " + serverSetting);
        }
    }

    public void kickAllPlayers(final String msg) {
        if (this.playerManager.getOnlinePlayers().isEmpty()) {
            this.logger.info("Lista graczy jest pusta");
            return;
        }
        this.playerManager.getOnlinePlayers().forEach(name -> this.kick(name, msg));
    }

    public void kick(final String who, final String reason) {
        if (this.playerManager.getOnlinePlayers().isEmpty()) {
            this.logger.info("Lista graczy jest pusta");
            return;
        }
        this.sendToConsole("kick " + who + " " + MessageUtil.colorize(reason));
    }

    public void tellrawToAll(final String msg) {
        if (this.playerManager.getOnlinePlayers().isEmpty()) {
            this.logger.info("Lista graczy jest pusta");
            return;
        }
        this.sendToConsole(MessageUtil.colorize("tellraw @a {\"rawtext\":[{\"text\":\"" + msg + "\"}]}"));
    }

    public void tellrawToPlayer(final String playerName, final String msg) {
        if (this.playerManager.getOnlinePlayers().isEmpty()) {
            this.logger.info("Lista graczy jest pusta");
            return;
        }
        this.sendToConsole(MessageUtil.colorize("tellraw " + playerName + " {\"rawtext\":[{\"text\":\"" + msg + "\"}]}"));
    }

    public void tellrawToAllAndLogger(final String prefix, final String msg, final LogState logState) {
        this.logger.logByState("[To Minecraft] " + msg, logState);
        if (!this.playerManager.getOnlinePlayers().isEmpty()) this.tellrawToAll(prefix + " " + msg);
    }

    public boolean isCanRun() {
        return this.canRun;
    }

    public void setCanRun(final boolean canRun) {
        this.canRun = canRun;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public boolean isEnabled() {
        return (this.process != null && this.process.isAlive());
    }

    public Process getProcess() {
        return this.process;
    }

    private boolean containsNotAllowedToFileLog(final String msg) {
        for (final String s : this.config.getLogConfig().getNoFile()) {
            if (msg.toLowerCase().contains(s.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private boolean containsNotAllowedToConsoleLog(final String msg) {
        for (final String s : this.config.getLogConfig().getNoConsole()) {
            if (msg.toLowerCase().contains(s.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private boolean containsNotAllowedToDiscordConsoleLog(final String msg) {
        for (final String s : this.config.getLogConfig().getNoDiscordConsole()) {
            if (msg.toLowerCase().contains(s.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}
