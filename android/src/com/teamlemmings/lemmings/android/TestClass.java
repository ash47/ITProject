package com.teamlemmings.lemmings.android;

import android.content.Context;
import android.content.Intent;

public class TestClass {
	public static Intent createQuery(Context context, String query, String value) {
		Intent i = new Intent(context, TestClass2.class);
		i.putExtra("QUERY", query);
		i.putExtra("VALUE", value);
		return i;
	} 
}
