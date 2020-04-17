package com.example.budgettracker;

public class TransactionModel {
    public int mId;
    public String mDate, mCategory;
    public double mAmount;

    public String toSQL() {
        StringBuilder sb = new StringBuilder("(");
        sb.append(mId).append(",");
        sb.append("\"").append(mDate).append("\"").append(",");
        sb.append(mAmount).append(",");
        sb.append("\"").append(mCategory).append("\"").append(");");
        return sb.toString();
    }
}
