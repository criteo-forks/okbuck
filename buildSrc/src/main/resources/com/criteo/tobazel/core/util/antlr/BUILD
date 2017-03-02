java_library(
    name = "org_antlr_antlr4_runtime_4_5",
    visibility = ["//visibility:public"],
    exports = ["@org_antlr_antlr4_runtime_4_5//jar"],
)

java_library(
    name = "org_antlr_antlr_runtime_3_5_2",
    visibility = ["//visibility:public"],
    exports = ["@org_antlr_antlr_runtime_3_5_2//jar"],
)

java_library(
    name = "org_antlr_ST4_4_0_8",
    visibility = ["//visibility:public"],
    exports = ["@org_antlr_ST4_4_0_8//jar"],
)

java_library(
    name = "org_antlr_antlr4",
    visibility = ["//visibility:public"],
    exports = ["@org_antlr_antlr4//jar"],
)

java_binary(
    name = "antlr_tool",
    main_class = "org.antlr.v4.Tool",
    visibility = ["//visibility:public"],
    runtime_deps = [":tool"],
)

java_library(
    name = "tool",
    exports = ["@org_antlr_antlr4//jar"],
    runtime_deps = [
        ":org_antlr_antlr4_runtime_4_5",
        ":org_antlr_ST4_4_0_8",
        ":org_antlr_antlr4",
    ],
)