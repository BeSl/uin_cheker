package org.besl.uin_cheker.aspect

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
//import org.springframework.boot.autoconfigure.info.ProjectInfoProperties
//import org.hibernate.query.sqm.tree.SqmNode.log
import org.springframework.stereotype.Component
import kotlin.math.log

@Aspect
@Component
class LoggingAspect(
) {

    @Around("execution(* org.besl.uin_cheker.service.*.*(..))")
    fun logServiceMethods(joinPoint: ProceedingJoinPoint): Any {
//        log.info("Вызов метода: ${joinPoint.signature}")
        val result = joinPoint.proceed()
//        log.info("Метод завершен: ${joinPoint.signature}")
        return result
    }
}