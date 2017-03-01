def addstattool():
    cmd = ["curl http://nexus.criteo.prod/content/repositories/criteo.moab.artifacts/com/criteo/hadoop/criteo-hadoop-prediction-adstattool/maven-metadata.xml > mvn.xml",
           "grep release mvn.xml | cut -f 2 -d'>' |cut -f 1 -d '<' > $@"]
    native.genrule(
       name = "getmoab",
       outs = ["generated-resources/main/prediction-binaries/cmoab"],
       cmd = " && ".join(cmd),
       visibility = ["//visibility:public", ],
    )
    cmd = ["MOAB=`cat $<`",
           "curl http://nexus.criteo.prod/content/repositories/criteo.moab.artifacts/com/criteo/hadoop/criteo-hadoop-prediction-adstattool/$${MOAB}/criteo-haddop-prediction-adstattool-$${MOAB}.tar.gz > $@"]
    native.genrule(
       name = "addstattool",
       srcs = ["generated-resources/main/prediction-binaries/cmoab"],
       outs = ["generated-resources/main/prediction-binaries/adstattool.tar.gz"],
       cmd = " && ".join(cmd),
       visibility = ["//visibility:public", ],
    )
