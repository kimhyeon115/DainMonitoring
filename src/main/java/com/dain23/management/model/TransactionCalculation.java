package com.dain23.management.model;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class TransactionCalculation {
	public int successApply;
	public int successParam;
	public List<Map<String, Object>> paramList;
}
