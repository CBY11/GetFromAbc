package model;

import jdk.nashorn.internal.scripts.JS;

public class AbcTaggedValue {
    public short tag;           // 表示数据种类的标记，使用uint8_t对应的Java类型
    public byte[] data;         // 根据不同的标记，data是不同类型的数据或者为空

    public AbcTaggedValue() {
    }
}

