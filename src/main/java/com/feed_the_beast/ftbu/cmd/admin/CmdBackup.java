package com.feed_the_beast.ftbu.cmd.admin;

import com.feed_the_beast.ftbl.api.cmd.CommandLM;
import com.feed_the_beast.ftbl.api.cmd.CommandLevel;
import com.feed_the_beast.ftbl.api.cmd.CommandSubLM;
import com.feed_the_beast.ftbl.util.BroadcastSender;
import com.feed_the_beast.ftbu.FTBULang;
import com.feed_the_beast.ftbu.config.FTBUConfigBackups;
import com.feed_the_beast.ftbu.world.Backups;
import latmod.lib.LMFileUtils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class CmdBackup extends CommandSubLM
{
    public static class CmdBackupStart extends CommandLM
    {
        public CmdBackupStart(String s)
        { super(s, CommandLevel.OP); }

        @Override
        public void execute(MinecraftServer server, ICommandSender ics, String[] args) throws CommandException
        {
            boolean b = Backups.run(ics);
            if(b)
            {
                FTBULang.backup_manual_launch.printChat(BroadcastSender.inst, ics.getName());

                if(!FTBUConfigBackups.use_separate_thread.getAsBoolean())
                {
                    Backups.postBackup();
                }
            }
            else
            {
                FTBULang.backup_already_running.printChat(ics);
            }
        }
    }

    public static class CmdBackupStop extends CommandLM
    {
        public CmdBackupStop(String s)
        { super(s, CommandLevel.OP); }

        @Override
        public void execute(MinecraftServer server, ICommandSender ics, String[] args) throws CommandException
        {
            if(Backups.thread != null)
            {
                Backups.thread.interrupt();
                Backups.thread = null;
                FTBULang.backup_stop.printChat(ics);
                return;
            }

            throw FTBULang.backup_not_running.commandError();
        }
    }

    public static class CmdBackupGetSize extends CommandLM
    {
        public CmdBackupGetSize(String s)
        { super(s, CommandLevel.OP); }

        @Override
        public void execute(MinecraftServer server, ICommandSender ics, String[] args) throws CommandException
        {
            String sizeW = LMFileUtils.getSizeS(ics.getEntityWorld().getSaveHandler().getWorldDirectory());
            String sizeT = LMFileUtils.getSizeS(Backups.backupsFolder);
            FTBULang.backup_size.printChat(ics, sizeW, sizeT);
        }
    }

    public CmdBackup()
    {
        super("backup", CommandLevel.OP);
        add(new CmdBackupStart("start"));
        add(new CmdBackupStop("stop"));
        add(new CmdBackupGetSize("getsize"));
    }
}