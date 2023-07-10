package me.andrewberger.graphql.annotations

import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
annotation class DataLoader(val key: KClass<*>, val type: KClass<*>)