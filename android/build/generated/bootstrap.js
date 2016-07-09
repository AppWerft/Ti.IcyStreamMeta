/**
 * Appcelerator Titanium Mobile
 * Copyright (c) 2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 *
 * Warning: This file is GENERATED, and should not be modified
 */
var bootstrap = kroll.NativeModule.require("bootstrap"),
	invoker = kroll.NativeModule.require("invoker"),
	Titanium = kroll.binding("Titanium").Titanium;

function moduleBootstrap(moduleBinding) {
	function lazyGet(object, binding, name, namespace) {
		return bootstrap.lazyGet(object, binding,
			name, namespace, moduleBinding.getBinding);
	}

	var module = moduleBinding.getBinding("de.appwerft.icystreammeta.IcyMetaModule")["Icystreammeta"];
	var invocationAPIs = module.invocationAPIs = [];
	module.apiName = "Icystreammeta";

	function addInvocationAPI(module, moduleNamespace, namespace, api) {
		invocationAPIs.push({ namespace: namespace, api: api });
	}

		addInvocationAPI(module, "Icystreammeta", "Icystreammeta", "createIcyMeta");
	addInvocationAPI(module, "Icystreammeta", "Icystreammeta", "createIcyClient");

			if (!("__propertiesDefined__" in module)) {		
		Object.defineProperties(module, {
			"IcyMeta": {
				get: function() {
					var IcyMeta = lazyGet(this, "de.appwerft.icystreammeta.IcyMetaProxy", "IcyMeta", "IcyMeta");
					return IcyMeta;
				},
				configurable: true
			},
			"IcyClient": {
				get: function() {
					var IcyClient = lazyGet(this, "de.appwerft.icystreammeta.IcyClientProxy", "IcyClient", "IcyClient");
					return IcyClient;
				},
				configurable: true
			},
		
		});
		module.constructor.prototype.createIcyMeta = function() {
			return new module.IcyMeta(arguments);
		}
		module.constructor.prototype.createIcyClient = function() {
			return new module.IcyClient(arguments);
		}
		}
		module.__propertiesDefined__ = true;
		return module;

}
exports.bootstrap = moduleBootstrap;
