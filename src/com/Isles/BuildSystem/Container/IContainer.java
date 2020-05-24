package com.Isles.BuildSystem.Container;

public interface IContainer {
    boolean isInherited();

    void setInherited(boolean inherited);

    boolean isUpdateTrigger();

    void setUpdateTrigger(boolean updateTrigger);

    boolean isLocal();

    void setLocal(boolean local);

    boolean isPublicSymbol();

    void setPublicSymbol(boolean publicSymbol);

    void setName(String name);

    String getName();

    boolean isConst();

    void setConst(boolean c);

}
