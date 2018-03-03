package cc.etherspace.example

import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ExampleModule::class])
interface ExampleComponent {
    fun inject(exampleApp: ExampleApp)
    fun inject(exampleApp: MainActivity)
}