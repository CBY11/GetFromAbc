package model;/*
 *model
 *Dell
 *2024 2024/7/25 9:16
 */

public class AbcForeignMethod extends AbcForeign{
    public int class_idx;//	uint16_t	一个指向该方法所从属的类的索引，指向一个在ClassRegionIndex中的位置，该位置的值是一个指向Class或ForeignClass的偏移量。
    public int reserved;//	uint16_t	方舟字节码文件内部使用的保留字段。
    public long name_off;//	uint32_t	一个偏移量，指向字符串，表示方法名称。
    public long index_data;//	uleb128	方法的MethodIndexData数据。
}
