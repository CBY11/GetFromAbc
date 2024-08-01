package model;/*
 *model
 *Dell
 *2024 2024/7/24 10:25
 */

public class AbcMethod {

    public static final byte FUNCTION = 0x1; //	普通函数。
    public static final byte NC_FUNCTION = 0x2; //		普通箭头函数。
    public static final byte GENERATOR_FUNCTION = 0x3; //	生成器函数。
    public static final byte ASYNC_FUNCTION = 0x4; //	异步函数。
    public static final byte ASYNC_GENERATOR_FUNCTION = 0x5; //	异步生成器函数。
    public static final byte ASYNC_NC_FUNCTION = 0x6; //	异步箭头函数。
    public static final byte CONCURRENT_FUNCTION = 0x7; //	并发函数。


    public int class_idx;    // uint16_t	一个指向该方法所从属的类的索引，指向一个在ClassRegionIndex中的位置，该位置的值是Type类型，是一个指向Class的偏移量。
    public int reserved;    // uint16_t	方舟字节码文件内部使用的保留字段。
    public long name_off;    // uint32_t	一个偏移量，指向字符串，表示方法名称。
    public String name;    // string	方法名称。
    public byte[] index_data;    // uleb128	方法的MethodIndexData数据。
    public int header_index;    // uint16_t	指向一个在IndexSection中的位置，该位置的值是IndexHeader。通过IndexHeader可以找到该方法引用的所有方法 (Method) 、字符串或字面量数组 (LiteralArray) 的偏移量。
    public byte function_kind;    // uint8_t	表示方法的函数类型 (FunctionKind) 。
    public byte index_data_reserved;    // uint8_t	方舟字节码文件内部使用的保留字段。


    public AbcTaggedValue[] method_data;    // TaggedValue[]	不定长度的数组，数组中每个元素都是TaggedValue类型，元素的标记是MethodTag类型，数组中的元素按照标记递增排序（0x00标记除外）。

    public AbcCode code;    // Code	方法的Code数据。
    public int src_lang = -1; // 源代码的语言-1 表示没说明， 0表示ArkTs、TS、JS
    public AbcDebugInfo debug_info;    // DebugInfo	方法的DebugInfo数据。
    public AbcAnnotation[] abcAnnotations;    // Annotation[]	不定长度的数组，数组中每个元素都是Annotation类型，表示方法的注解。

}
