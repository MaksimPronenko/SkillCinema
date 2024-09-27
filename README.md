# Проект SkillCinema
Удобное и красивое мобильное приложение для поиска информации о фильмах и сериалах, а также для создания коллекций фильмов.
Данные получаются со стороннего бесплатного API, и, по мере загрузки, сохраняются в базу данных Room на устройстве. Перед каждым обращением к API проверяется наличие в собственной базе данных.

Разработал на основе технического задания и дизайн-макета в Figma:
[макет в Figma](https://www.figma.com/file/MxuwrBFd27nstefTfSeLCo/skillcinema?type=design&node-id=0-1&mode=design&t=HtEh9KxbG3Vqlwoe-0).

Использованные в проекте технологии:
- Coroutines;
- паттерн MVVM;
- Hilt;
- Navigation;
- Retrofit;
- Room;
- JSON;
- Glide.

Приложение не коммерческое, но вполне достойное по функционалу и качеству на фоне аналогов.

<img src="https://github.com/MaksimPronenko/SkillCinema/assets/105295402/24f9dab2-3a8e-4948-9e8b-9fb091cd509b" width="270" height="585">
<img src="https://github.com/MaksimPronenko/SkillCinema/assets/105295402/402c828c-db5f-421f-8ea6-89d91cbe0725" width="270" height="585">
<img src="https://github.com/MaksimPronenko/SkillCinema/assets/105295402/b8f2fe9f-6a17-4486-9c62-62d1f3ee5e53" width="270" height="585">
<img src="https://github.com/MaksimPronenko/SkillCinema/assets/105295402/0cb88518-aac5-42a5-9aea-edaa61ffda08" width="270" height="585">
<img src="https://github.com/MaksimPronenko/SkillCinema/assets/105295402/9f279b4e-9cd3-43e2-b02e-fb2b8ca5f271" width="270" height="585">
<img src="https://github.com/MaksimPronenko/SkillCinema/assets/105295402/1b458ab2-6766-47b1-9334-dd196febba0c" width="270" height="585">
<img src="https://github.com/MaksimPronenko/SkillCinema/assets/105295402/73ec0768-c15a-47e0-a7de-27421a098831" width="270" height="585">
<img src="https://github.com/MaksimPronenko/SkillCinema/assets/105295402/e243aae1-884f-4527-80c6-f3c74d62c6a4" width="270" height="585">
<img src="https://github.com/MaksimPronenko/SkillCinema/assets/105295402/7e70a903-6824-40d7-93f8-e7e3cf668120" width="270" height="585">
<img src="https://github.com/MaksimPronenko/SkillCinema/assets/105295402/bb1ff43a-0a81-403b-84b5-b8385821e5fe" width="270" height="585">
<img src="https://github.com/MaksimPronenko/SkillCinema/assets/105295402/c3bfc2be-6930-4596-8b53-bfc2a43628db" width="270" height="585">
<img src="https://github.com/MaksimPronenko/SkillCinema/assets/105295402/4620a52c-903b-4fe4-9353-4f6aa89f95a4" width="270" height="585">
<img src="https://github.com/MaksimPronenko/SkillCinema/assets/105295402/45a9377b-d09c-4a28-a845-cc1fc145b311" width="270" height="585">
<img src="https://github.com/MaksimPronenko/SkillCinema/assets/105295402/7b578163-86b7-47b4-a08d-85f27ce03452" width="270" height="585">
<img src="https://github.com/MaksimPronenko/SkillCinema/assets/105295402/4d5ca731-40c7-4698-a6a2-95497d317c40" width="270" height="585">
<img src="https://github.com/MaksimPronenko/SkillCinema/assets/105295402/35485357-5679-4ba7-bf2e-9074af3a9f90" width="270" height="585">
<img src="https://github.com/MaksimPronenko/SkillCinema/assets/105295402/a15f6ea6-6ee7-422d-a243-f44addb90aaa" width="270" height="585">
<img src="https://github.com/MaksimPronenko/SkillCinema/assets/105295402/2e9a080e-00a4-416a-a9dc-31a67be86215" width="270" height="585">
<img src="https://github.com/MaksimPronenko/SkillCinema/assets/105295402/2dcb7c41-77fc-4a0b-bd0e-cf334f958d63" width="270" height="585">
<img src="https://github.com/MaksimPronenko/SkillCinema/assets/105295402/21558eed-72f9-46c3-93c4-160da9d47393" width="270" height="585">
<img src="https://github.com/MaksimPronenko/SkillCinema/assets/105295402/1dd4df40-6937-400e-be1c-cb1cfa287348" width="270" height="585">


## Инструкция по запуску приложения

Приложение написано на Android Studio + gradle.

1. Открыть проект на GitHub, нажать кнопку "Code" и скопировать ссылку HTTPS.
   
2. Открыть папку, в которую будем клонировать приложение.
   
3. Открыть GitBash, набрать "git clone", вставить скопированную ссылку, нажимем "Enter", дождаться завершения процесса клонирования.
  
4. Запустить Android Studio. Выбрать "File", затем в выпавшем списке нажать "Open", выбрать клонированное приложение, в той папке, где его разместили. Нажать "OK". Приложение начнёт загружаться: внизу справа появится индикатор прогресса.

5. По мере загрузки справа снизу появятся сообщения с рекомендациями об обновлениях. Принять их и обновить.
   
6. Когда всё загрузилось, нажать Device Manager. Затем нажать "Create Virtual Device" (значок "+").
   
7. По умолчанию открывается вкладка "Phone". В ней выбрать устройство, которое нужноэмулировать. Например, средний по размеру "Pixel 5". Можно выбрать и другой телефон, приложение сделано для устройства любого размера.

8. Далее выбрать API Level. Например, API 34 "UpsideDownCake". В правой части окна могут появиться рекомендации. Например, может появиться сообщение "HAXM is not installed. Install HAXM" (речь идёт о механизме виртуализации для компьютеров на базе процессоров Intel). Установить. Если появляется окно с предложением выбрать объём оперативной памяти для эмулятора, выбрать рекомендованный. Другие рекомендации тоже нужно исполнить.

9. Если SDK Component для нужного уровня API не установлен, запустить его установку, нажав кнопку справа от имени API. По завершении утсановки нажать "Next".

10. В открывшемся окне выбрать Startap Orientation Portrait. Нажать "Finish".

11. В верхней части окна Andoio появится имя созданного виртуального устройства. Правее него располагается кнопка запуска приложения (зелёная стрелка вправо). Нажать её. В правой нижней части окна Android Studio появится индикация прогресса загрузки. Первая загрузка приложения на новый эмулятор может занять несколько минут. По завершении приложение откроется на эмуляторе.
