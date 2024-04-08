package me.indian.bds;

import gs.mclo.api.Log;
import gs.mclo.api.MclogsClient;
import me.indian.bds.config.sub.log.LogConfig;
import me.indian.bds.logger.Logger;

public class McLog {

    private final Logger logger;
    private final LogConfig logConfig;
    private final MclogsClient mclogsClient;

    public McLog(final BDSAutoEnable bdsAutoEnable) {
        this.logger = bdsAutoEnable.getLogger();
        this.logConfig = bdsAutoEnable.getAppConfigManager().getLogConfig();
        this.mclogsClient = new MclogsClient("BDS-Auto-Enable", bdsAutoEnable.getProjectVersion(), bdsAutoEnable.getVersionManager().getLoadedVersion());
    }

    public void sendCurrentLog() {
        //TODO: Zaktualizować do 4.0.2 gdy debile zrobią wszytko dobrze XD
        if (this.logConfig.isSendLogs()) {
            try {
                this.logger.info("&aWysłano logi servera do&b mclo.gs&a link:&1 " + this.mclogsClient.uploadLog(new Log(this.logger.getLogFile().toPath())).getUrl());
            } catch (final Exception exception) {
                this.logger.error("&cNie udało się przekazać logów servera do&b mclo.gs", exception);
            }
        } else {
            this.logger.debug("Wysyłąnie logów na&b https://mclo.gs/&r jest wyłączone");
        }
    }
}
