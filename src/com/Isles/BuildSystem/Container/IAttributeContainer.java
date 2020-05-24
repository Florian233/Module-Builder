package com.Isles.BuildSystem.Container;

import java.util.List;

public interface IAttributeContainer<T> extends IContainer {

    List<T> getValue();

    void setValue(List<T> value);

    void clear();

    void add(T value);

    void add(List<T> value);

    void assign(T value);

    void assign(List<T> value);

}
