package model;/*
 *model
 *Dell
 *2024 2024/7/24 9:59
 */

public class AbcFieldTag {
    public static final short NOTHING = 0x00; // 数量：1 格式：none 拥有此标记的TaggedValue，是其所在class_data的最后一项。
    public static final short INT_VALUE = 0x01;// 数量：0-1	格式：sleb128	拥有此标记的TaggedValue的data是boolean、byte、short、char、int、。
    public static final short VALUE = 0x02;// 数量：0-1	格式：uint32_t	拥有此标记的TaggedValue的data的类型为Value formats中的FLOAT或ID。

}
