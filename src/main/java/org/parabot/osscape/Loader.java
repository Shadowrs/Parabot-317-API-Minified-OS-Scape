package org.parabot.osscape;

import org.ethan.oss.hook.Hook;
import org.ethan.oss.parameters.OSScapeParameters;
import org.ethan.oss.stub.Stub;
import org.ethan.oss.utils.Utilities;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.parabot.core.Context;
import org.parabot.core.Core;
import org.parabot.core.Directories;
import org.parabot.core.asm.ASMClassLoader;
import org.parabot.core.asm.adapters.AddInterfaceAdapter;
import org.parabot.core.asm.hooks.HookFile;
import org.parabot.core.ui.components.VerboseLoader;
import org.parabot.environment.api.utils.WebUtil;
import org.parabot.environment.scripts.Script;
import org.parabot.environment.servers.ServerManifest;
import org.parabot.environment.servers.ServerProvider;
import org.parabot.environment.servers.Type;
import org.parabot.osscape.accessors.Client;

import javax.swing.*;
import java.applet.Applet;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Everel, JKetelaar
 */
@ServerManifest(author = "Ethan, Everel & JKetelaar", name = "OS-Scape", type = Type.INJECTION, version = 0.1)
public class Loader extends ServerProvider {
    private boolean extended = true;

    private static Client client = null;

    public static Client getClient() {
        if (client == null) {
            Objenesis objenesis = new ObjenesisStd();

            try {
                client = (Client) objenesis.newInstance(Context.getInstance().getASMClassLoader().loadClass("oss/iIIiiiiIiI"));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        return client;
    }

    @Override
    public Applet fetchApplet() {
        try {
            Hook.getInstance().init();

            final Context           context     = Context.getInstance();
            final ASMClassLoader    classLoader = context.getASMClassLoader();
            final Class<?>          clientClass = classLoader.loadClass(Context.getInstance().getServerProviderInfo().getClientClass());
            final OSScapeParameters parser      = new OSScapeParameters();
            Object                  instance    = clientClass.newInstance();

            Utilities.handleOSScape(parser, classLoader);

            Applet applet = (Applet) instance;
            applet.setStub(new Stub(parser));

            return applet;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    @Override
    public URL getJar() {
        String name   = "os-scape.jar";
        File   target = new File(Directories.getCachePath(), name);
        if (!target.exists()) {
            try {
                WebUtil.downloadFile(new URL("https://cdn.os-scape.com/clients/game.jar"), target, VerboseLoader.get());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        return WebUtil.toURL(target);
    }

    @Override
    public void addMenuItems(JMenuBar bar) {

    }

    @Override
    public void injectHooks() {
        AddInterfaceAdapter.setAccessorPackage("org/parabot/osscape/accessors/");
        try {
            super.injectHooks();
        } catch (Exception e) {
            if (Core.inVerboseMode()) {
                e.printStackTrace();
            }
            this.extended = false;
            super.injectHooks();
        }
    }

    @Override
    public void initScript(Script script) {
//        ScriptEngine.getInstance().setScript(script);
//        ScriptEngine.getInstance().init();
    }

    @Override
    public HookFile getHookFile() {
        if (this.extended) {
            return new HookFile(Context.getInstance().getServerProviderInfo().getHookFile(), HookFile.TYPE_XML);
        } else {
            return new HookFile(Context.getInstance().getServerProviderInfo().getHookFile(), HookFile.TYPE_XML);
        }
    }

    public void unloadScript(Script script) {
//        ScriptEngine.getInstance().unload();
    }

    @Override
    public void init() {
    }
}