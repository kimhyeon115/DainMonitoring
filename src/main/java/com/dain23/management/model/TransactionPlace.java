package com.dain23.management.model;

import lombok.Data;

@Data
public class TransactionPlace {
	public String placeCode;
	public boolean needsTableCreation = false;
}
