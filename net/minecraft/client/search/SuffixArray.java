package net.minecraft.client.search;

import org.apache.logging.log4j.LogManager;
import java.util.Set;
import it.unimi.dsi.fastutil.ints.IntSet;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.util.Collections;
import it.unimi.dsi.fastutil.Swapper;
import it.unimi.dsi.fastutil.Arrays;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.List;
import org.apache.logging.log4j.Logger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class SuffixArray<T>
{
    private static final boolean PRINT_COMPARISONS;
    private static final boolean PRINT_ARRAY;
    private static final Logger LOGGER;
    protected final List<T> objects;
    private final IntList e;
    private final IntList f;
    private IntList g;
    private IntList h;
    private int maxTextLength;
    
    public SuffixArray() {
        this.objects = Lists.newArrayList();
        this.e = (IntList)new IntArrayList();
        this.f = (IntList)new IntArrayList();
        this.g = (IntList)new IntArrayList();
        this.h = (IntList)new IntArrayList();
    }
    
    public void add(final T object, final String text) {
        this.maxTextLength = Math.max(this.maxTextLength, text.length());
        final int integer3 = this.objects.size();
        this.objects.add(object);
        this.f.add(this.e.size());
        for (int integer4 = 0; integer4 < text.length(); ++integer4) {
            this.g.add(integer3);
            this.h.add(integer4);
            this.e.add((int)text.charAt(integer4));
        }
        this.g.add(integer3);
        this.h.add(text.length());
        this.e.add(-1);
    }
    
    public void reload() {
        final int integer1 = this.e.size();
        final int[] arr2 = new int[integer1];
        final int[] arr3 = new int[integer1];
        final int[] arr4 = new int[integer1];
        final int[] arr5 = new int[integer1];
        final IntComparator intComparator6 = (IntComparator)new IntComparator() {
            public int compare(final int integer1, final int integer2) {
                if (arr3[integer1] == arr3[integer2]) {
                    return Integer.compare(arr4[integer1], arr4[integer2]);
                }
                return Integer.compare(arr3[integer1], arr3[integer2]);
            }
            
            public int compare(final Integer integer1, final Integer integer2) {
                return this.compare((int)integer1, (int)integer2);
            }
        };
        final Swapper swapper7 = (integer4, integer5) -> {
            if (integer4 != integer5) {
                int integer6 = arr3[integer4];
                arr3[integer4] = arr3[integer5];
                arr3[integer5] = integer6;
                integer6 = arr4[integer4];
                arr4[integer4] = arr4[integer5];
                arr4[integer5] = integer6;
                integer6 = arr5[integer4];
                arr5[integer4] = arr5[integer5];
                arr5[integer5] = integer6;
            }
        };
        for (int integer2 = 0; integer2 < integer1; ++integer2) {
            arr2[integer2] = this.e.getInt(integer2);
        }
        for (int integer2 = 1, integer3 = Math.min(integer1, this.maxTextLength); integer2 * 2 < integer3; integer2 *= 2) {
            for (int integer4 = 0; integer4 < integer1; ++integer4) {
                arr3[integer4] = arr2[integer4];
                arr4[integer4] = ((integer4 + integer2 < integer1) ? arr2[integer4 + integer2] : -2);
                arr5[integer4] = integer4;
            }
            Arrays.quickSort(0, integer1, intComparator6, swapper7);
            for (int integer4 = 0; integer4 < integer1; ++integer4) {
                if (integer4 > 0 && arr3[integer4] == arr3[integer4 - 1] && arr4[integer4] == arr4[integer4 - 1]) {
                    arr2[arr5[integer4]] = arr2[arr5[integer4 - 1]];
                }
                else {
                    arr2[arr5[integer4]] = integer4;
                }
            }
        }
        final IntList intList10 = this.g;
        final IntList intList11 = this.h;
        this.g = (IntList)new IntArrayList(intList10.size());
        this.h = (IntList)new IntArrayList(intList11.size());
        for (final int integer6 : arr5) {
            this.g.add(intList10.getInt(integer6));
            this.h.add(intList11.getInt(integer6));
        }
        if (SuffixArray.PRINT_ARRAY) {
            this.debugPrintArray();
        }
    }
    
    private void debugPrintArray() {
        for (int integer1 = 0; integer1 < this.g.size(); ++integer1) {
            SuffixArray.LOGGER.debug("{} {}", integer1, this.a(integer1));
        }
        SuffixArray.LOGGER.debug("");
    }
    
    private String a(final int integer) {
        final int integer2 = this.h.getInt(integer);
        final int integer3 = this.f.getInt(this.g.getInt(integer));
        final StringBuilder stringBuilder4 = new StringBuilder();
        for (int integer4 = 0; integer3 + integer4 < this.e.size(); ++integer4) {
            if (integer4 == integer2) {
                stringBuilder4.append('^');
            }
            final int integer5 = this.e.get(integer3 + integer4);
            if (integer5 == -1) {
                break;
            }
            stringBuilder4.append((char)integer5);
        }
        return stringBuilder4.toString();
    }
    
    private int a(final String string, final int integer) {
        final int integer2 = this.f.getInt(this.g.getInt(integer));
        final int integer3 = this.h.getInt(integer);
        for (int integer4 = 0; integer4 < string.length(); ++integer4) {
            final int integer5 = this.e.getInt(integer2 + integer3 + integer4);
            if (integer5 == -1) {
                return 1;
            }
            final char character7 = string.charAt(integer4);
            final char character8 = (char)integer5;
            if (character7 < character8) {
                return -1;
            }
            if (character7 > character8) {
                return 1;
            }
        }
        return 0;
    }
    
    public List<T> findAll(final String text) {
        final int integer2 = this.g.size();
        int integer3 = 0;
        int integer4 = integer2;
        while (integer3 < integer4) {
            final int integer5 = integer3 + (integer4 - integer3) / 2;
            final int integer6 = this.a(text, integer5);
            if (SuffixArray.PRINT_COMPARISONS) {
                SuffixArray.LOGGER.debug("comparing lower \"{}\" with {} \"{}\": {}", text, integer5, this.a(integer5), integer6);
            }
            if (integer6 > 0) {
                integer3 = integer5 + 1;
            }
            else {
                integer4 = integer5;
            }
        }
        if (integer3 < 0 || integer3 >= integer2) {
            return Collections.<T>emptyList();
        }
        final int integer5 = integer3;
        integer4 = integer2;
        while (integer3 < integer4) {
            final int integer6 = integer3 + (integer4 - integer3) / 2;
            final int integer7 = this.a(text, integer6);
            if (SuffixArray.PRINT_COMPARISONS) {
                SuffixArray.LOGGER.debug("comparing upper \"{}\" with {} \"{}\": {}", text, integer6, this.a(integer6), integer7);
            }
            if (integer7 >= 0) {
                integer3 = integer6 + 1;
            }
            else {
                integer4 = integer6;
            }
        }
        final int integer6 = integer3;
        final IntSet intSet7 = (IntSet)new IntOpenHashSet();
        for (int integer8 = integer5; integer8 < integer6; ++integer8) {
            intSet7.add(this.g.getInt(integer8));
        }
        final int[] arr8 = intSet7.toIntArray();
        java.util.Arrays.sort(arr8);
        final Set<T> set9 = Sets.newLinkedHashSet();
        for (final int integer9 : arr8) {
            set9.add(this.objects.get(integer9));
        }
        return Lists.newArrayList(set9);
    }
    
    static {
        PRINT_COMPARISONS = Boolean.parseBoolean(System.getProperty("SuffixArray.printComparisons", "false"));
        PRINT_ARRAY = Boolean.parseBoolean(System.getProperty("SuffixArray.printArray", "false"));
        LOGGER = LogManager.getLogger();
    }
}
