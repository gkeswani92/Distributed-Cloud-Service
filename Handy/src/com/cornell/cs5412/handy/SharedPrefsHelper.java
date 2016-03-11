package com.cornell.cs5412.handy;

import java.io.Serializable;

public class SharedPrefsHelper implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7540414474978926719L;

	public SharedPrefsHelper() 
	{ 
		super();
	}
	
	public void saveString(String key, String value) 
	{ 
		Globals.prefEditor.putString(key, value);
		Globals.prefEditor.commit();
	}

	public void saveInt(String key, int value) 
	{ 
		Globals.prefEditor.putInt(key, value);
		Globals.prefEditor.commit();
	}
	

	public void saveBoolean (String key, boolean value)
	{
		Globals.prefEditor.putBoolean(key, value);
		Globals.prefEditor.commit();
	}
	

	public void saveFloat (String key, float value)
	{ 
		Globals.prefEditor.putFloat(key, value);
		Globals.prefEditor.commit();
	}
	
	public void saveLong (String key, Long value)
	{ 
		Globals.prefEditor.putLong(key, value);
		Globals.prefEditor.commit();
	}
	

	public String getString(String key)
	{ 
		if(contains(key)) 
			return Globals.appPref.getString(key, "");
		else 
			return "";
	}
	
	public boolean getBoolean(String key)
	{
		if (contains(key)) 
			return Globals.appPref.getBoolean(key, false);
		else 
			return false;
	}
	
	public int getInt(String key) 
	{ 
		if (contains(key)) 
			return Globals.appPref.getInt(key, -1);
		else 
			return -1;
	}
	
	public float getFloat(String key) 
	{ 
		if (contains(key)) 
			return Globals.appPref.getFloat(key, -1);
		else 
			return -1;
	}
	
	public long getLong(String key) 
	{ 
		if (contains(key)) 
			return Globals.appPref.getLong(key, -1);
		else 
			return -1;
	}
	
	public boolean contains (String key) 
	{ 
		try 
		{ 
			return Globals.appPref.contains(key);
		} 
		catch (Exception e) 
		{ 
			return false;
		}
	}

	public void clear() 
	{ 
		Globals.prefEditor.clear().commit();
	}
	
	public boolean Remove(String key)
	{		
		if ( Globals.prefEditor.remove(key) != null )
		{
			Globals.prefEditor.commit();
			return true;
		}
		else
			return false;
		
	}
}
