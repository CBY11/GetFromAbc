package model;

public class AbcClass {
    public long offset; //该class在文件中的偏移量，用于type查找
    public String name;                     // Class的名称，命名遵循TypeDescriptor语法
    public long reserved;                   // 方舟字节码文件内部使用的保留字段，使用uint32_t对应的Java类型
    public long access_flags;               // Class的访问标志，是ClassAccessFlag的组合，使用uleb128编码
    public long num_fields;                 // Class的字段的数量，使用uleb128编码
    public long num_methods;                // Class的方法的数量，使用uleb128编码
    public int src_lang = -1;                 // 源代码的语言-1 表示没说明， 0表示ArkTs、TS、JS
    public String src_file_name;                  // 源代码的文件名
    public AbcTaggedValue[] class_data;    // 不定长度的数组，数组中每个元素都是TaggedValue类型
    public AbcField[] fields;              // Class的字段的数组，数组中每一个元素都是Field类型
    public AbcMethod[] methods;            // Class的方法的数组，数组中每一个元素都是Method类型
}



