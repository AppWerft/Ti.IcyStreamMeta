package de.appwerft.icymetaclient;

import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.titanium.TiApplication;

@Kroll.module(name = "Icymetaclient", id = "de.appwerft.icymetaclient")
public class IcymetaclientModule extends KrollModule {

	public IcymetaclientModule() {
		super();
	}

	@Kroll.onAppCreate
	public static void onAppCreate(TiApplication app) {

	}

}
