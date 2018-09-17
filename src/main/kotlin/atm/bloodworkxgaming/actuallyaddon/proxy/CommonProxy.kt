package atm.bloodworkxgaming.actuallyaddon.proxy

import atm.bloodworkxgaming.bloodyLib.proxy.CommonProxy

abstract class SubCommonProxy : CommonProxy()

class ServerProxy : SubCommonProxy()

class ClientProxy : SubCommonProxy()