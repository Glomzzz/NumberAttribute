package com.skillw.numberatt.internal.command

import com.skillw.numberatt.NumberAttribute
import org.bukkit.command.CommandSender
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand

@CommandHeader(name = "na", permission = "na.command")
object NACommand {


    @CommandBody
    val main = mainCommand {
        execute<CommandSender> { sender, _, _ ->
            NumberAttribute.reload()
            sender.sendMessage("Reloaded!")
        }
    }
}