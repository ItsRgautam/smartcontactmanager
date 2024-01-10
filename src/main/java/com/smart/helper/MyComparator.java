package com.smart.helper;

import java.util.Comparator;

import org.springframework.stereotype.Component;

import com.smart.entities.Contact;

@Component
public class MyComparator implements Comparator<Contact> {

	@Override
	public int compare(Contact o1, Contact o2) {
		String s1 = o1.getName();
		String s2 = o2.getName();
		return s1.compareTo(s2);
	}

}
