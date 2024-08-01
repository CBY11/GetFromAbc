package model;/*
 *model
 *Dell
 *2024 2024/7/25 10:12
 */

public class AbcIndexHeader {
    public long start_off;//	uint32_t	该区域的起始偏移量。
    public long end_off;//	uint32_t	该区域的结束偏移量。
    public long class_region_idx_size; //	uint32_t	该区域的ClassRegionIndex中元素的数量，最大值为65536。
    public long class_region_idx_off; //	uint32_t	一个偏移量，指向ClassRegionIndex。
    public long method_string_literal_region_idx_size; //	uint32_t	该区域的MethodStringLiteralRegionIndex中元素的数量，最大值为65536。
    public long method_string_literal_region_idx_off; //	uint32_t	一个偏移量，指向MethodStringLiteralRegionIndex。
    public long reserved1; //	uint32_t	方舟字节码文件内部使用的保留字段。
    public long reserved2; //	uint32_t	方舟字节码文件内部使用的保留字段。
    public long reserved3; //	uint32_t	方舟字节码文件内部使用的保留字段。
    public long reserved4; //	uint32_t	方舟字节码文件内部使用的保留字段。
    public AbcType[] types;// ClassRegionIndex结构的作用是允许通过更紧凑的索引，找到对应的Type。

    public AbcMethodStringLiteral[] method_string_literals;// MethodStringLiteralRegionIndex结构的作用是允许通过更紧凑的索引，找到对应的MethodStringLiteral。

}
