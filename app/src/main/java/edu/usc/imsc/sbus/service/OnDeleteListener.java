package edu.usc.imsc.sbus.service;

/**
 * Created by Mengjia on 16/1/2.
 */
public interface OnDeleteListener {
    public abstract  boolean isCanDelete(int position);
    public abstract void onDelete(int id);
    public abstract void onBack();
}
