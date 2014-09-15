/**
 * 
 */
package com.basus.fins.ui;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;

import com.basus.fins.account.Account;

/**
 * @author sambit
 *
 */
public class AccountComboBoxModel extends DefaultComboBoxModel {
	private HashSet<Account> list = null;
	
	public AccountComboBoxModel(HashSet<Account> list, int selectedAccountId) {
		super(new Vector<Account>(list));
		this.list = list;
		if (null == list) {
			return;
		}

		Iterator<Account> it = list.iterator();
		Account acct = null;
		
		while (it.hasNext()) {
			acct = it.next();
			if (acct.getAccountId() == selectedAccountId) {
				this.setSelectedItem(acct);
			}
		}
	}
}
