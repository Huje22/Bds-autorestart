package me.indian.bds.command.defaults;

import me.indian.bds.BDSAutoEnable;
import me.indian.bds.command.Command;
import me.indian.bds.command.CommandSender;
import me.indian.bds.watchdog.module.BackupModule;

import java.nio.file.Path;

public class BackupCommand extends Command {

    private final BDSAutoEnable bdsAutoEnable;

    public BackupCommand(final BDSAutoEnable bdsAutoEnable) {
        super("backup", "info o backup");
        this.bdsAutoEnable = bdsAutoEnable;
    }

    @Override
    public boolean onExecute(final CommandSender sender, final String[] args, final boolean isOp) {
        final BackupModule backupModule = this.bdsAutoEnable.getWatchDog().getBackupModule();
        if (backupModule == null) {
            this.sendMessage("&cNie udało się uzyskać&b Modułu Backupów");
            return true;
        }

        if (!backupModule.isEnabled()) {
            this.sendMessage("&aBackupy są wyłączone");
            return true;
        }

        if (args.length == 0) {
            if (backupModule.getBackups().size() == 0) {
                this.sendMessage("&aBrak backupów");
                return true;
            }

            for (final Path path : backupModule.getBackups()) {
                this.sendMessage("&a" + path.getFileName() + " Rozmiar: ` " + backupModule.getBackupSize(path.toFile(), false) + "`");
            }
        } else if (args[0].equals("do")) {
            this.sendMessage("&aCoś kiedyś tu będziesz");
        }

        return true;
    }
}