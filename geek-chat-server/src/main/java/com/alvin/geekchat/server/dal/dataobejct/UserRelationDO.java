package com.alvin.geekchat.server.dal.dataobejct;

public class UserRelationDO {
    private Integer id;
    private Integer relationUser1;
    private Integer relationUser2;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRelationUser1() {
        return relationUser1;
    }

    public void setRelationUser1(Integer relationUser1) {
        this.relationUser1 = relationUser1;
    }

    public Integer getRelationUser2() {
        return relationUser2;
    }

    public void setRelationUser2(Integer relationUser2) {
        this.relationUser2 = relationUser2;
    }
}
