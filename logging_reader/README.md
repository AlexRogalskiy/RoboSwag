logging_reader
=====

### Общее описание

Модуль служит для получания на устройстве файла с логами разного приоритета, их анализа и возможности отправить файл через стандартный шеринг.


### Пример

Для вызова диалогового окна, позволяющего сохранять, читать и отправлять логи, достаточно вызвать:
```kotlin
DebugLogsDialogFragment().show(parentFragmentManager, null)
```