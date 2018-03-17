#verbose=true

bean.id.1=ceuta
ceuta.class=interceptor.MairieE
ceuta.property.1=nombreDHabitants
ceuta.property.1.param.1=2000

bean.id.2=proxy1
proxy1.class=interceptor.ProxyFactory
proxy1.property.1=type
#proxy1.property.1.param.1=interceptor.MairieE
proxy1.property.1.param.1=interceptor.MairieI.class
proxy1.property.2=interceptor
proxy1.property.2.param.1=interceptor1

bean.id.3=interceptor1
interceptor1.class=interceptor.Interceptor
interceptor1.property.1=target
interceptor1.property.1.param.1=ceuta
