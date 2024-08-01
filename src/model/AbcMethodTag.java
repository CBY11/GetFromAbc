package model;/*
 *model
 *Dell
 *2024 2024/7/23 17:42
 */

public class AbcMethodTag {
    // 名称	值	数量	格式	说明
    public static final short NOTHING = 0x00;// 1	none	拥有此标记的TaggedValue，是其所在method_data的最后一项。
    public static final short CODE = 0x01;// 0-1	uint32_t	拥有此标记的TaggedValue的data是一个偏移量，指向Code，表示方法的代码段。
    public static final short SOURCE_LANG = 0x02;// 0-1	uint8_t	拥有此标记的TaggedValue的data是0，表示源码语言是ArkTS/TS/JS。
    public static final short DEBUG_INFO = 0x05;// 0-1	uint32_t	拥有此标记的TaggedValue的data是一个偏移量，指向DebugInfo，表示方法的调试信息。
    public static final short ANNOTATION = 0x06;// >=0	uint32_t	拥有此标记的TaggedValue的data是一个偏移量，指向Annotation， 表示方法的注解。
}
