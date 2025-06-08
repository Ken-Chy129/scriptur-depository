package cn.ken.mxbean;

import java.lang.management.*;
import java.util.Collection;
import java.util.concurrent.ThreadPoolExecutor;

import static cn.ken.mxbean.TTable.Align.LEFT;
import static cn.ken.mxbean.TTable.Align.RIGHT;

/**
 * @author Ken-Chy129
 * @date 2025/5/20
 */
public class TestMXBean {

    private final RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
    private final ClassLoadingMXBean classLoadingMXBean = ManagementFactory.getClassLoadingMXBean();
    private final CompilationMXBean compilationMXBean = ManagementFactory.getCompilationMXBean();
    private final Collection<GarbageCollectorMXBean> garbageCollectorMXBeans = ManagementFactory.getGarbageCollectorMXBeans();
    private final Collection<MemoryManagerMXBean> memoryManagerMXBeans = ManagementFactory.getMemoryManagerMXBeans();
    private final MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
    //    private final Collection<MemoryPoolMXBean> memoryPoolMXBeans = ManagementFactory.getMemoryPoolMXBeans();
    private final OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
    private final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

    public static void main(String[] args) {
        TestMXBean testMXBean = new TestMXBean();
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor();
        threadPoolExecutor.
        System.out.println(testMXBean.drawRuntimeTable());
        System.out.println(testMXBean.drawClassLoadingTable());
        System.out.println(testMXBean.drawCompilationTable());
        System.out.println(testMXBean.drawGarbageCollectorsTable());
        System.out.println(testMXBean.drawMemoryManagersTable());
        System.out.println(testMXBean.drawMemoryTable());
        System.out.println(testMXBean.drawOperatingSystemMXBeanTable());
        System.out.println(testMXBean.drawThreadTable());

    }

    private TKv createKVView() {
        return new TKv(
                new TTable.ColumnDefine(25, false, RIGHT),
                new TTable.ColumnDefine(70, false, LEFT)
        );
    }

    private String drawRuntimeTable() {
        final TKv view = createKVView()
                .add("MACHINE-NAME", runtimeMXBean.getName())
                .add("JVM-START-TIME", SimpleDateFormatHolder.getInstance().format(runtimeMXBean.getStartTime()))
                .add("MANAGEMENT-SPEC-VERSION", runtimeMXBean.getManagementSpecVersion())
                .add("SPEC-NAME", runtimeMXBean.getSpecName())
                .add("SPEC-VENDOR", runtimeMXBean.getSpecVendor())
                .add("SPEC-VERSION", runtimeMXBean.getSpecVersion())
                .add("VM-NAME", runtimeMXBean.getVmName())
                .add("VM-VENDOR", runtimeMXBean.getVmVendor())
                .add("VM-VERSION", runtimeMXBean.getVmVersion())
                .add("INPUT-ARGUMENTS", toCol(runtimeMXBean.getInputArguments()))
                .add("CLASS-PATH", runtimeMXBean.getClassPath())
                .add("BOOT-CLASS-PATH", runtimeMXBean.isBootClassPathSupported() ?
                        runtimeMXBean.getBootClassPath() :
                        "This JVM does not support boot class path.")
                //TODO: add "MODULE-PATH" for JDK 9
                .add("LIBRARY-PATH", runtimeMXBean.getLibraryPath());

        return view.rendering();
    }

    private String drawClassLoadingTable() {
        final TKv view = createKVView()
                .add("LOADED-CLASS-COUNT", classLoadingMXBean.getLoadedClassCount())
                .add("TOTAL-LOADED-CLASS-COUNT", classLoadingMXBean.getTotalLoadedClassCount())
                .add("UNLOADED-CLASS-COUNT", classLoadingMXBean.getUnloadedClassCount())
                .add("IS-VERBOSE", classLoadingMXBean.isVerbose());
        return view.rendering();
    }

    private String drawCompilationTable() {
        final TKv view = createKVView()
                .add("NAME", compilationMXBean.getName());

        if (compilationMXBean.isCompilationTimeMonitoringSupported()) {
            view.add("TOTAL-COMPILE-TIME", compilationMXBean.getTotalCompilationTime() + "(ms)");
        }
        return view.rendering();
    }

    private String drawGarbageCollectorsTable() {
        final TKv view = createKVView();

        for (GarbageCollectorMXBean garbageCollectorMXBean : garbageCollectorMXBeans) {
            view.add(garbageCollectorMXBean.getName() + "\n[count/time]",
                    garbageCollectorMXBean.getCollectionCount() + "/" + garbageCollectorMXBean.getCollectionTime() + "(ms)");
        }

        return view.rendering();
    }

    private String drawMemoryManagersTable() {
        final TKv view = createKVView();

        for (final MemoryManagerMXBean memoryManagerMXBean : memoryManagerMXBeans) {
            if (memoryManagerMXBean.isValid()) {
                final String name = memoryManagerMXBean.isValid()
                        ? memoryManagerMXBean.getName()
                        : memoryManagerMXBean.getName() + "(Invalid)";


                view.add(name, toCol(memoryManagerMXBean.getMemoryPoolNames()));
            }
        }

        return view.rendering();
    }

    private String drawMemoryTable() {
        final TKv view = createKVView();

        view.add("HEAP-MEMORY-USAGE\n[committed/init/max/used]",
                memoryMXBean.getHeapMemoryUsage().getCommitted()
                        + "/" + memoryMXBean.getHeapMemoryUsage().getInit()
                        + "/" + memoryMXBean.getHeapMemoryUsage().getMax()
                        + "/" + memoryMXBean.getHeapMemoryUsage().getUsed()
        );

        view.add("NO-HEAP-MEMORY-USAGE\n[committed/init/max/used]",
                memoryMXBean.getNonHeapMemoryUsage().getCommitted()
                        + "/" + memoryMXBean.getNonHeapMemoryUsage().getInit()
                        + "/" + memoryMXBean.getNonHeapMemoryUsage().getMax()
                        + "/" + memoryMXBean.getNonHeapMemoryUsage().getUsed()
        );

        view.add("PENDING-FINALIZE-COUNT", memoryMXBean.getObjectPendingFinalizationCount());
        return view.rendering();
    }

    private String drawOperatingSystemMXBeanTable() {
        final TKv view = createKVView();
        view
                .add("OS", operatingSystemMXBean.getName())
                .add("ARCH", operatingSystemMXBean.getArch())
                .add("PROCESSORS-COUNT", operatingSystemMXBean.getAvailableProcessors())
                .add("LOAD-AVERAGE", operatingSystemMXBean.getSystemLoadAverage())
                .add("VERSION", operatingSystemMXBean.getVersion());
        return view.rendering();
    }

    private String drawThreadTable() {
        final TKv view = createKVView();

        view
                .add("COUNT", threadMXBean.getThreadCount())
                .add("DAEMON-COUNT", threadMXBean.getDaemonThreadCount())
                .add("LIVE-COUNT", threadMXBean.getPeakThreadCount())
                .add("STARTED-COUNT", threadMXBean.getTotalStartedThreadCount())
        ;
        return view.rendering();
    }

    private String toCol(Collection<String> strings) {
        final StringBuilder colSB = new StringBuilder();
        if (strings.isEmpty()) {
            colSB.append("[]");
        } else {
            for (String str : strings) {
                colSB.append(str).append("\n");
            }
        }
        return colSB.toString();
    }

    private String toCol(String... stringArray) {
        final StringBuilder colSB = new StringBuilder();
        if (null == stringArray
                || stringArray.length == 0) {
            colSB.append("[]");
        } else {
            for (String str : stringArray) {
                colSB.append(str).append("\n");
            }
        }
        return colSB.toString();
    }
}
