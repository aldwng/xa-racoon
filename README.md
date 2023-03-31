# xa-racoon
Racoon is a small tool for configuring query mapping, which running on zk.
It supports real time config editing, and wrong format checking.If any user commits a failure on configuration, racoon will recover the config with the latest available version on zk.
