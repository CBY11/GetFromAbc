package model;

public class AbcHeader {
    public String magic; // 文件头魔数
    public long checksum; // 校验和
    public String version; // 版本号
    public long fileSize; // 文件大小
    public long foreign_off; // 外部区域偏移量
    public long foreign_size; // 外部区域大小
    public long num_classes; // ClassIndex 结构中元素的数量
    public long class_idx_off; // 指向 ClassIndex 的偏移量
    public long num_lnps; // LineNumberProgramIndex 结构中元素的数量
    public long lnp_idx_off; // 指向 LineNumberProgramIndex 的偏移量
    public long reserved1; // 保留字段1
    public long reserved2; // 保留字段2
    public long num_index_regions; // IndexSection 结构中元素的数量
    public long index_section_off; // 指向 IndexSection 的偏移量

    public long[] lineNumberProgramIndex;

    public AbcHeader() {
    }

    public AbcHeader(String magic, long checksum, String version, long fileSize, long foreign_off, long foreign_size, long num_classes, long class_idx_off, long num_lnps, long lnp_idx_off, long reserved1, long reserved2, long num_index_regions, long indexSectionOff) {

        this.magic = magic;
        this.checksum = checksum;
        this.version = version;
        this.fileSize = fileSize;
        this.foreign_off = foreign_off;
        this.foreign_size = foreign_size;
        this.num_classes = num_classes;
        this.class_idx_off = class_idx_off;
        this.num_lnps = num_lnps;
        this.lnp_idx_off = lnp_idx_off;
        this.reserved1 = reserved1;
        this.reserved2 = reserved2;
        this.num_index_regions = num_index_regions;
        this.index_section_off = indexSectionOff;
    }

    @Override
    public String toString() {
        return "model.AbcHeader{" +
                "\nmagic='" + magic + '\'' +
                ",\n checksum=" + checksum +
                ",\n version='" + version + '\'' +
                ",\n fileSize=" + fileSize +
                ",\n foreignOff=" + foreign_off +
                ",\n foreignSize=" + foreign_size +
                ",\n numClasses=" + num_classes +
                ",\n classIdxOff=" + class_idx_off +
                ",\n numLnps=" + num_lnps +
                ",\n lnpIdxOff=" + lnp_idx_off +
                ",\n reserved1=" + reserved1 +
                ",\n reserved2=" + reserved2 +
                ",\n numIndexRegions=" + num_index_regions +
                ",\n indexSectionOff=" + index_section_off +
                '}';
    }
}
