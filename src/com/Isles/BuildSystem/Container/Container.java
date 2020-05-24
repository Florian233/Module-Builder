package com.Isles.BuildSystem.Container;

public abstract class Container implements IContainer {

    protected boolean inherited = false;

    protected boolean updateTrigger = false;

    protected boolean local = false;

    protected boolean publicSymbol = true;

    protected boolean input = false;

    protected String name = "";

    protected boolean constVar = false;

    @Override
    public boolean isInherited() {
        return inherited;
    }

    @Override
    public void setInherited(boolean inherited) {
        this.inherited = inherited;
    }

    @Override
    public boolean isUpdateTrigger() {
        return updateTrigger;
    }

    @Override
    public void setUpdateTrigger(boolean updateTrigger) {
        this.updateTrigger = updateTrigger;
    }

    @Override
    public boolean isLocal() {
        return local;
    }

    @Override
    public void setLocal(boolean local) {
        this.local = local;
    }

    @Override
    public boolean isPublicSymbol() {
        return publicSymbol;
    }

    @Override
    public void setPublicSymbol(boolean publicSymbol) {
        this.publicSymbol = publicSymbol;
    }

    @Override
    public void setName(String name) { this.name = name; }

    @Override
    public String getName() {return name;}

    @Override
    public boolean isConst(){
        return constVar;
    }

    @Override
    public void setConst(boolean c) {
        constVar = c;
    }

}
