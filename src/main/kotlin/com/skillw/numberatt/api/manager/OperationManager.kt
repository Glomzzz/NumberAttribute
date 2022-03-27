package com.skillw.numberatt.api.manager

import com.skillw.numberatt.api.operation.BaseOperation
import com.skillw.pouvoir.api.manager.Manager
import com.skillw.pouvoir.api.map.LowerKeyMap

abstract class OperationManager : LowerKeyMap<BaseOperation>(), Manager {

}