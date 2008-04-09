/*
 * Created on Jul 24, 2004
 *
 
   	Copyright 2004 Curtis Moxley
   	This file is part of Espresso3D.
 
   	Espresso3D is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Espresso3D is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with Espresso3D.  If not, see <http://www.gnu.org/licenses/>.
 */
package espresso3d.engine.input;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import espresso3d.engine.E3DEngine;
import espresso3d.engine.base.E3DEngineItem;
import espresso3d.engine.input.base.IE3DInputCallback;
import espresso3d.engine.logger.E3DEngineLogger;


/**
 * @author espresso3d
 * 
 * The basic input handler class.  An instance of this is created within the engine for you
 */
public class E3DInputHandler extends E3DEngineItem 
{
	public static final Integer KEY_0 = new Integer(Keyboard.KEY_0);
	public static final Integer KEY_1 = new Integer(Keyboard.KEY_1);
	public static final Integer KEY_2 = new Integer(Keyboard.KEY_2);
	public static final Integer KEY_3 = new Integer(Keyboard.KEY_3);
	public static final Integer KEY_4 = new Integer(Keyboard.KEY_4);
	public static final Integer KEY_5 = new Integer(Keyboard.KEY_5);
	public static final Integer KEY_6 = new Integer(Keyboard.KEY_6);
	public static final Integer KEY_7 = new Integer(Keyboard.KEY_7);
	public static final Integer KEY_8 = new Integer(Keyboard.KEY_8);
	public static final Integer KEY_9 = new Integer(Keyboard.KEY_9);
	public static final Integer KEY_A = new Integer(Keyboard.KEY_A);
	public static final Integer KEY_ADD = new Integer(Keyboard.KEY_ADD);
	public static final Integer KEY_APOSTROPHE = new Integer(Keyboard.KEY_APOSTROPHE);
	public static final Integer KEY_APPS = new Integer(Keyboard.KEY_APPS);
	public static final Integer KEY_AT = new Integer(Keyboard.KEY_AT);
	public static final Integer KEY_AX = new Integer(Keyboard.KEY_AX);
	public static final Integer KEY_B = new Integer(Keyboard.KEY_B);
	public static final Integer KEY_BACK = new Integer(Keyboard.KEY_BACK);
	public static final Integer KEY_BACKSLASH = new Integer(Keyboard.KEY_BACKSLASH);
	public static final Integer KEY_C = new Integer(Keyboard.KEY_C);
	public static final Integer KEY_CAPITAL = new Integer(Keyboard.KEY_CAPITAL);
	public static final Integer KEY_CIRCUMFLEX = new Integer(Keyboard.KEY_CIRCUMFLEX);
	public static final Integer KEY_COLON = new Integer(Keyboard.KEY_COLON);
	public static final Integer KEY_COMMA = new Integer(Keyboard.KEY_COMMA);
	public static final Integer KEY_CONVERT = new Integer(Keyboard.KEY_CONVERT);
	public static final Integer KEY_D = new Integer(Keyboard.KEY_D);
	public static final Integer KEY_DECIMAL = new Integer(Keyboard.KEY_DECIMAL);
	public static final Integer KEY_DELETE = new Integer(Keyboard.KEY_DELETE);
	public static final Integer KEY_DIVIDE = new Integer(Keyboard.KEY_DIVIDE);
	public static final Integer KEY_DOWN = new Integer(Keyboard.KEY_DOWN);
	public static final Integer KEY_E = new Integer(Keyboard.KEY_E);
	public static final Integer KEY_END = new Integer(Keyboard.KEY_END);
	public static final Integer KEY_EQUALS = new Integer(Keyboard.KEY_EQUALS);
	public static final Integer KEY_ESCAPE = new Integer(Keyboard.KEY_ESCAPE);
	public static final Integer KEY_F = new Integer(Keyboard.KEY_F);
	public static final Integer KEY_F1 = new Integer(Keyboard.KEY_F1);
	public static final Integer KEY_F2 = new Integer(Keyboard.KEY_F2);
	public static final Integer KEY_F3 = new Integer(Keyboard.KEY_F3);
	public static final Integer KEY_F4 = new Integer(Keyboard.KEY_F4);
	public static final Integer KEY_F5 = new Integer(Keyboard.KEY_F5);
	public static final Integer KEY_F6 = new Integer(Keyboard.KEY_F6);
	public static final Integer KEY_F7 = new Integer(Keyboard.KEY_F7);
	public static final Integer KEY_F8 = new Integer(Keyboard.KEY_F8);
	public static final Integer KEY_F9 = new Integer(Keyboard.KEY_F9);
	public static final Integer KEY_F10 = new Integer(Keyboard.KEY_F10);
	public static final Integer KEY_F11 = new Integer(Keyboard.KEY_F11);
	public static final Integer KEY_F12 = new Integer(Keyboard.KEY_F12);
	public static final Integer KEY_F13 = new Integer(Keyboard.KEY_F13);
	public static final Integer KEY_F14 = new Integer(Keyboard.KEY_F14);
	public static final Integer KEY_F15 = new Integer(Keyboard.KEY_F15);
	public static final Integer KEY_G = new Integer(Keyboard.KEY_G);
	public static final Integer KEY_GRAVE = new Integer(Keyboard.KEY_GRAVE);
	public static final Integer KEY_H = new Integer(Keyboard.KEY_H);
	public static final Integer KEY_HOME = new Integer(Keyboard.KEY_HOME);
	public static final Integer KEY_I = new Integer(Keyboard.KEY_I);
	public static final Integer KEY_INSERT = new Integer(Keyboard.KEY_INSERT);
	public static final Integer KEY_J = new Integer(Keyboard.KEY_J);
	public static final Integer KEY_K = new Integer(Keyboard.KEY_K);
	public static final Integer KEY_KANA = new Integer(Keyboard.KEY_KANA);
	public static final Integer KEY_KANJI = new Integer(Keyboard.KEY_KANJI);
	public static final Integer KEY_L = new Integer(Keyboard.KEY_L);
	public static final Integer KEY_LBRACKET = new Integer(Keyboard.KEY_LBRACKET);
	public static final Integer KEY_LCONTROL = new Integer(Keyboard.KEY_LCONTROL);
	public static final Integer KEY_LEFT = new Integer(Keyboard.KEY_LEFT);
	public static final Integer KEY_LMENU = new Integer(Keyboard.KEY_LMENU);
	public static final Integer KEY_LSHIFT = new Integer(Keyboard.KEY_LSHIFT);
	public static final Integer KEY_LWIN = new Integer(Keyboard.KEY_LWIN);
	public static final Integer KEY_M = new Integer(Keyboard.KEY_M);
	public static final Integer KEY_MINUS = new Integer(Keyboard.KEY_MINUS);
	public static final Integer KEY_MULTIPLY = new Integer(Keyboard.KEY_MULTIPLY);
	public static final Integer KEY_N = new Integer(Keyboard.KEY_N);
	public static final Integer KEY_NEXT = new Integer(Keyboard.KEY_NEXT);
	public static final Integer KEY_NOCONVERT = new Integer(Keyboard.KEY_NOCONVERT);
	public static final Integer KEY_NONE = new Integer(Keyboard.KEY_NONE);
	public static final Integer KEY_NUMLOCK = new Integer(Keyboard.KEY_NUMLOCK);
	public static final Integer KEY_NUMPAD0 = new Integer(Keyboard.KEY_NUMPAD0);
	public static final Integer KEY_NUMPAD1 = new Integer(Keyboard.KEY_NUMPAD1);
	public static final Integer KEY_NUMPAD2 = new Integer(Keyboard.KEY_NUMPAD2);
	public static final Integer KEY_NUMPAD3 = new Integer(Keyboard.KEY_NUMPAD3);
	public static final Integer KEY_NUMPAD4 = new Integer(Keyboard.KEY_NUMPAD4);
	public static final Integer KEY_NUMPAD5 = new Integer(Keyboard.KEY_NUMPAD5);
	public static final Integer KEY_NUMPAD6 = new Integer(Keyboard.KEY_NUMPAD6);
	public static final Integer KEY_NUMPAD7 = new Integer(Keyboard.KEY_NUMPAD7);
	public static final Integer KEY_NUMPAD8 = new Integer(Keyboard.KEY_NUMPAD8);
	public static final Integer KEY_NUMPAD9 = new Integer(Keyboard.KEY_NUMPAD9);
	public static final Integer KEY_NUMPADCOMMA = new Integer(Keyboard.KEY_NUMPADCOMMA);
	public static final Integer KEY_NUMPADENTER = new Integer(Keyboard.KEY_NUMPADENTER);
	public static final Integer KEY_NUMPADEQUALS = new Integer(Keyboard.KEY_NUMPADEQUALS);
	public static final Integer KEY_O = new Integer(Keyboard.KEY_O);
	public static final Integer KEY_P = new Integer(Keyboard.KEY_P);
	public static final Integer KEY_PAUSE = new Integer(Keyboard.KEY_PAUSE);
	public static final Integer KEY_PERIOD = new Integer(Keyboard.KEY_PERIOD);
	public static final Integer KEY_POWER = new Integer(Keyboard.KEY_POWER);
	public static final Integer KEY_PRIOR = new Integer(Keyboard.KEY_PRIOR);
	public static final Integer KEY_Q = new Integer(Keyboard.KEY_Q);
	public static final Integer KEY_R = new Integer(Keyboard.KEY_R);
	public static final Integer KEY_RBRACKET = new Integer(Keyboard.KEY_RBRACKET);
	public static final Integer KEY_RCONTROL = new Integer(Keyboard.KEY_RCONTROL);
	public static final Integer KEY_RETURN = new Integer(Keyboard.KEY_RETURN);
	public static final Integer KEY_RIGHT = new Integer(Keyboard.KEY_RIGHT);
	public static final Integer KEY_RMENU = new Integer(Keyboard.KEY_RMENU);
	public static final Integer KEY_RSHIFT = new Integer(Keyboard.KEY_RSHIFT);
	public static final Integer KEY_RWIN = new Integer(Keyboard.KEY_RWIN);
	public static final Integer KEY_S = new Integer(Keyboard.KEY_S);
	public static final Integer KEY_SCROLL = new Integer(Keyboard.KEY_SCROLL);
	public static final Integer KEY_SEMICOLON = new Integer(Keyboard.KEY_SEMICOLON);
	public static final Integer KEY_SLASH = new Integer(Keyboard.KEY_SLASH);
	public static final Integer KEY_SLEEP = new Integer(Keyboard.KEY_SLEEP);
	public static final Integer KEY_SPACE = new Integer(Keyboard.KEY_SPACE);
	public static final Integer KEY_STOP = new Integer(Keyboard.KEY_STOP);
	public static final Integer KEY_SUBTRACT = new Integer(Keyboard.KEY_SUBTRACT);
	public static final Integer KEY_SYSRQ = new Integer(Keyboard.KEY_SYSRQ);
	public static final Integer KEY_T = new Integer(Keyboard.KEY_T);
	public static final Integer KEY_TAB = new Integer(Keyboard.KEY_TAB);
	public static final Integer KEY_U = new Integer(Keyboard.KEY_U);
	public static final Integer KEY_UNDERLINE = new Integer(Keyboard.KEY_UNDERLINE);
	public static final Integer KEY_UNLABELED = new Integer(Keyboard.KEY_UNLABELED);
	public static final Integer KEY_UP = new Integer(Keyboard.KEY_UP);
	public static final Integer KEY_V = new Integer(Keyboard.KEY_V);
	public static final Integer KEY_W = new Integer(Keyboard.KEY_W);
	public static final Integer KEY_X = new Integer(Keyboard.KEY_X);
	public static final Integer KEY_Y = new Integer(Keyboard.KEY_Y);
	public static final Integer KEY_YEN = new Integer(Keyboard.KEY_YEN);
	public static final Integer KEY_Z = new Integer(Keyboard.KEY_Z);

	public static final Integer MOUSE_BUTTON_LEFT = new Integer(0);
	public static final Integer MOUSE_BUTTON_RIGHT = new Integer(1);
	public static final Integer MOUSE_BUTTON_MIDDLE = new Integer(2);
	
	private E3DKeyboardHandler keyboardHandler;
	private E3DMouseButtonHandler mouseButtonHandler;
	private E3DMouseMovementHandler mouseMovementHandler;
	
	public E3DInputHandler(E3DEngine engine)
	{
		super(engine);

		engine.getLogger().writeLine(E3DEngineLogger.SEVERITY_INFO, "Initializing input handler");

		try{
		    Keyboard.create();
			Mouse.create();
		}catch(LWJGLException e){
			engine.getLogger().writeLine(E3DEngineLogger.SEVERITY_WARNING, "Error initializing input handler in engine initialization. Input handling will not be available. " + e.getMessage() );
		}
		
		keyboardHandler = new E3DKeyboardHandler();
		mouseButtonHandler = new E3DMouseButtonHandler();
		mouseMovementHandler = new E3DMouseMovementHandler();
	}

	/**
	 * Bind a key to a IE3DInputCallback implementing class.  Once bound,
	 * when this key is pressed, callback's onInput method will be called
	 * 
	 * @param key
	 * @param callback
	 */
	public void bindKeyToHandler(Integer key, IE3DInputCallback callback)
	{
		keyboardHandler.bindKeyToHandler(key, callback);
	}
	

	/**
	 * Bind a mouse button to a IE3DInputCallback implementing class.  Once bound,
	 * when this mouseButton is pressed, callback's onInput method will be called
	 * @param mouseButton
	 * @param callback
	 */
	public void bindMouseButtonToHandler(Integer mouseButton, IE3DInputCallback callback)
	{
	    mouseButtonHandler.bindMouseButtonToHandler(mouseButton, callback);
	}
	
	/**
	 * Bind mouse movement to a IE3DInputCallback implementing class.
	 * @param callback
	 */
	public void bindMouseMovementToHandler(IE3DInputCallback callback)
	{
	    mouseMovementHandler.bindMouseMovementToHandler(callback);
	}
	
	/**
	 * Call this once per frame to have all input callbacks fired off if
	 * their registered bindings have taken place (eg: a key has been pressed)
	 */
	public void checkInput()
	{
		keyboardHandler.checkKeyboardInput();
		mouseButtonHandler.checkMouseButtonInput();
	    mouseMovementHandler.checkMouseMovementInput();
	}

	/**
	 * Can be used to check if a specified key is down (KEY_...).  This is normally
	 * only used to see if another key is down when a key down callback is fired.  For example,
	 * it might be used to to check if SHIFT is held down in some other key's bound callback.
	 * @param keyCode  One of the static KEY_ variables
	 */
	public boolean isKeyDown(Integer keyCode)
	{
	    return keyboardHandler.isKeyDown(keyCode);
	}

	/**
	 * Can be used to check if a specified key is down (KEY_...).  This is normally
	 * only used to see if another key is down when a key down callback is fired.  For example,
	 * @param buttonCode One of the static MOUSE_BUTTON_ variables.
	 */
	public boolean isMouseButtonDown(Integer buttonCode)
	{
	    return mouseButtonHandler.isMouseButtonDown(buttonCode);
	}
	
	/**
	 * If the mouse has been set to hidden, this will return true, otherwise true
	 */
	public boolean isMouseCursorHidden()
	{
	    return Mouse.isGrabbed();
	}
	
	/**
	 * Set whether or not to hide the mouse cursor
	 * @param hidden
	 */
	public void setMouseCursorHidden(boolean hidden)
	{
	    Mouse.setGrabbed(hidden);
	}

	/**
	 * Returns the mouse X value between 0 and screenWidth - 1
	 * @return
	 */
	public int getMousePositionX()
	{
	    return Mouse.getX();
	}

	/**
	 * Returns the mouse Y  value between 0 and screenHeight - 1
	 * @return
	 */
	public int getMousePositionY()
	{
	    return Mouse.getY();
	}

}
