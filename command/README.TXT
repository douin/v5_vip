#verbose=true

bean.id.1=invoker
invoker.class=command.Invoker
invoker.property.1=commande
invoker.property.1.param.1=cmd

bean.id.2=lampeDeLAmphi
lampeDeLAmphi.class=command.Light

bean.id.3=cmd
cmd.class=command.CommandLight
cmd.property.1=lampe
cmd.property.1.param.1=lampeDeLAmphi

