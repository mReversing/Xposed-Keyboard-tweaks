package com.appsofawesome.keyboardutilities;


import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import android.view.KeyEvent;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import android.os.IBinder;
import de.robv.android.xposed.XposedBridge;


/**
 * Hooks into the method where it detects alt tab presses and stops android from eating them. 
 * @author Lordofstuff
 *
 */
public class KeyConsumeHook implements IXposedHookLoadPackage{
	
	private boolean catchAlt = true;
	private boolean catchMeta = true;
//	private boolean catchWinTab = false;


	@Override
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
		if (!lpparam.packageName.equals("android")) {
			XposedBridge.log("keytweaker:We are in package: " + lpparam.packageName);
			return;
		}
		XposedBridge.log("keytweaker-start");
		//get classes needed to hook method
		//Class WindowManagerClass = findClass("com.android.internal.policy.impl.PhoneWindowManager", lpparam.classLoader);
		//Class WindowStateClass = findClass("com.android.server.policy.WindowManagerPolicy$WindowState", lpparam.classLoader);

		// @see https://android.googlesource.com/platform/frameworks/base/+/master/services/core/java/com/android/server/policy/PhoneWindowManager.java
		// @see https://android.googlesource.com/platform/frameworks/base/+/master/services/core/java/com/android/server/policy/WindowManagerPolicy.java
		Class WindowManagerClass = findClass("com.android.server.policy.PhoneWindowManager", lpparam.classLoader);

		//actually hook the method
		findAndHookMethod(WindowManagerClass, "interceptKeyBeforeDispatching", IBinder.class, KeyEvent.class, int.class, hook1);
		XposedBridge.log("keytweaker-start-complete");
	}

	//the callback that overrides behavior in certain circumstances. 
	XC_MethodHook hook1 = new XC_MethodHook() {
		@Override
		protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
			
			KeyEvent event = (KeyEvent) param.args[1];
			// Action: 0 down 1 up
			// Meta: 117/125 ALT:57/56 ctrl:113/29 shift:59/42
			//XposedBridge.log("keytweaker:Press " + event.getKeyCode() + " | " + event.getDisplayLabel() + " | " + event.getAction() + " | " + event.getScanCode() + " | " + event.isMetaPressed() + " | " + event.isAltPressed() + " | " + event.getMetaState());

			if (catchAlt && (event.getKeyCode() == KeyEvent.KEYCODE_ALT_LEFT || event.isAltPressed())) {
				param.setResult(0); //tell it it was not handled and let the app handle it.
			}

			if (catchMeta && event.getKeyCode() == KeyEvent.KEYCODE_META_LEFT) {
				param.setResult(0); //tell it it was not handled and let the app handle it.
			}
			if (catchMeta && event.isMetaPressed()) {
				// eg. win + e down/up when 'e' is pressed
				param.setResult(0); //tell it it was not handled and let the app handle it.
			}
			//XposedBridge.log("keytweaker:result " + param.getResult());

		}
		
		@Override
		protected void afterHookedMethod(MethodHookParam param) throws Throwable {

		}
	};
}
	
	
	
		
		
		
	
	
	
	
	
	

	