# Usage

You can type `./buckw targets` to get a list of targets that can be build. The generated `.buckconfig.local` file will have some aliases setup to build your apps without having to type the rulename. i.e you can do things like `./buckw build appDebug another-appPaidRelease` etc. The plugin also generates an empty `.buckconfig` file if it does not exist. You can customize the settings in the `.buckconfig` file by using the various [options](https://buckbuild.com/concept/buckconfig.html)

## Configuring OkBuck
Example configuration
```gradle
okbuck {
    buildToolVersion "23.0.3"
    target "android-23"
    linearAllocHardLimit = [
            app: 16 * 1024 * 1024
    ]
    primaryDexPatterns = [
            app: [
                    '^com/github/okbuilds/okbuck/example/AppShell^',
                    '^com/github/okbuilds/okbuck/example/BuildConfig^',
                    '^android/support/multidex/',
                    '^com/facebook/buck/android/support/exopackage/',
                    '^com/github/promeg/xlog_android/lib/XLogConfig^',
                    '^com/squareup/leakcanary/LeakCanary^',
            ]
    ]
    exopackage = [
            appDebug: true
    ]
    appLibDependencies = [
            'appProd': [
                    'buck-android-support',
                    'com.android.support:multidex',
                    'libraries/javalibrary:main',
                    'libraries/common:paidRelease',
            ],
            'appDev': [
                    'buck-android-support',
                    'com.android.support:multidex',
                    'libraries/javalibrary:main',
                    'libraries/common:freeDebug',
            ]
    ]
    annotationProcessors = [
            "local-apt-dependency": ['com.okbuilds.apt.ExampleProcessor']
    ]
    buckProjects = project.subprojects
    extraBuckOpts = [
        'appDebug', [ 
            "binary": ["trim_resource_ids = True"]
        ]
    ]

    wrapper {
        repo = 'https://github.com/OkBuilds/buck.git'
        remove = ['.buckconfig.local', "**/BUCK"]
        keep = [".okbuck/**/BUCK"]
    }
}
```

+  `buildToolVersion` specifies the version of the Android SDK Build-tools, defaults to `23.0.3`
+  `target` specifies the Android compile sdk version, default is `android-23`
+  `linearAllocHardLimit` and `primaryDexPatterns` are used to configure options used by buck for multidex apps. For more details about multidex configuration, please read the
[Multidex wiki](https://github.com/OkBuilds/OkBuck/wiki/Multidex-Configuration-Guide).
+  `exopackage` and `appLibDependencies` are used for
configuring buck's exopackage mode. For more details about exopackage configuration, 
please read the [Exopackage wiki](https://github.com/OkBuilds/OkBuck/wiki/Exopackage-Configuration-Guide), if you don't need exopackage, you can ignore these parameters
+ `annotationProcessors` is used to depend on annotation processors declared locally as another gradle module in the same project.
+  `buckProjects` is a set of projects to generate buck files for. Default is all sub projects.
+  `extraBuckOpts` provides a hook to add additional configuration options for buck [android_binary](https://buckbuild.com/rule/android_binary.html) rules
+  `wrapper` is used to configure creation of the buck wrapper script.
 - `repo` - The git url of any custom buck fork. Default is [OkBuilds/buck](https://github.com/OkBuilds/buck)
 - `remove` - List of file patterns to clean up by wrapper before running `okbuck`. Default is `['.buckconfig.local', '**/BUCK']`
 - `keep` - List of file patterns to not clean up by the wrapper before running `okbuck`. This may be useful if you made manual modifications to some buck files and would like to keep them intact while regenerating the configuration for other projects. Default is `['.okbuck/**/BUCK']`.
+ The keys used to configure various options can be for 
 - All buildTypes and flavors i.e `app`
 - All buildTypes of a particular flavor i.e 'appDemo'
 - All flavors of a particular buildType i.e 'appDebug'
 - A particular variant (buildType + flavor combination) i,e 'appDemoRelease'