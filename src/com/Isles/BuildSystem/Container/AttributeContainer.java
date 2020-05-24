package com.Isles.BuildSystem.Container;

import java.util.ArrayList;
import java.util.List;

public class AttributeContainer<T> extends Container implements IAttributeContainer<T> {

    private List<T> value;

    public AttributeContainer() {
        this.value = new ArrayList<>();
    }

    @Override
    public List<T> getValue() {
        return value;
    }

    @Override
    public void setValue(List<T> value) {
        this.value = value;
    }

    @Override
    public void clear() {
        value.clear();
    }

    @Override
    public void add(T value) {
        this.value.add(value);
    }

    @Override
    public void add(List<T> value) {
        this.value.addAll(value);
    }

    @Override
    public void assign(T value) {
        this.value.clear();
        this.value.add(value);
    }

    @Override
    public void assign(List<T> value) {
        this.value.clear();
        this.value.addAll(value);
    }

}
