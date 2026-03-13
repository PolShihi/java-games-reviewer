


Отличная работа с переходом на Angular! Я проанализировал новый JSON. У тебя **15 issue**, и на этот раз они затрагивают как старый React-код, так и новый Angular-код.

Важный момент: среди них появились **2 настоящих бага (BUG)**, связанных с потенциальным `null / undefined`. Их нужно исправить в первую очередь, так как они могут сломать приложение (белый экран).

Я сгруппировал исправления по папкам и файлам. Можешь скопировать этот текст и сохранить как `SONAR_FIXES_V2.md`.

---

# Отчет по исправлениям SonarCloud (React + Angular)

## ⚛️ ЧАСТЬ 1: Фронтенд на React

### 📁 1. `src/components/reviews/AddReviewDialog.jsx`
*   **Строка 142 (BUG 🐛):** `TypeError can be thrown as "initialReview" might be null or undefined here.`
    *   **Проблема:** Ты обращаешься к свойству объекта (например, `initialReview.score`), но `initialReview` может быть пустым. Если это произойдет, приложение упадет с ошибкой.
    *   **Как исправить:** Используй **опциональную цепочку (optional chaining)**. Добавь знак вопроса перед точкой. 
    *   *Пример:* Замени `initialReview.score` на `initialReview?.score`. Либо оберни в условие `if (initialReview) { ... }`.

### 📁 2. `src/components/system-requirements/AddRequirementDialog.jsx`
*   **Строка 195 (BUG 🐛):** `TypeError can be thrown as "initialRequirement" might be null or undefined here.`
    *   **Проблема:** Абсолютно такая же, как в предыдущем пункте, но с `initialRequirement`.
    *   **Как исправить:** Замени обращения на `initialRequirement?.названиеПоля`.
*   **Строка 126 (Критическая):** `Reduce Cognitive Complexity from 16 to the 15 allowed.`
    *   **Проблема:** Снова слишком сложная функция (перегрузка мозга). В ней слишком много `if / else`, тернарных операторов `? :` или логических `&&`.
    *   **Как исправить:** Раздроби эту большую функцию на две поменьше. Например, если там идет сложная валидация разных полей, вынеси валидацию каждого поля в отдельную функцию `validateCpu()`, `validateRam()` и т.д.

---

## 🅰️ ЧАСТЬ 2: Фронтенд на Angular

### 📁 3. `src/app/pages/game-details-page/game-details-page.component.ts`
*   **Строки 91, 144, 195:** `Prefer globalThis over window.`
    *   **Проблема:** Использование `window.scrollTo` или `window.confirm`.
    *   **Как исправить:** Замени слово `window.` на `globalThis.`.

### 📁 4. `src/app/components/reference-manager-dialog/reference-manager-dialog.component.ts`
*   **Строки 3 и 4:** `'@angular/forms' imported multiple times.`
    *   **Проблема:** У тебя два импорта из одного и того же пакета на разных строках.
    *   **Как исправить:** Объедини их в одну строку.
    *   *Было:* 
      ```typescript
      import { FormGroup } from '@angular/forms';
      import { FormBuilder } from '@angular/forms';
      ```
    *   *Стало:* 
      ```typescript
      import { FormGroup, FormBuilder } from '@angular/forms';
      ```
*   **Строка 60 (и 192, 305):** `Replace this union type with a type alias.`
    *   **Проблема:** У тебя часто повторяется длинный тип (Union Type), например `string | number | boolean` или что-то похожее. Sonar просит не копипастить его, а вынести в Type Alias (псевдоним).
    *   **Как исправить:** Прямо над классом компонента (или в отдельном файле моделей) напиши:
      ```typescript
      type MyCustomType = string | number | boolean; // подставь свои типы сюда
      ```
      А затем в строках 60, 192 и 305 замени длинную запись на `MyCustomType`.
*   **Строка 306:** `Prefer globalThis over window.`
    *   **Как исправить:** Замени `window.` на `globalThis.`.

### 📁 5. `src/app/pages/game-form-page/game-form-page.component.ts`
*   **Строка 189:** `Prefer using an optional chain expression instead...`
    *   **Проблема:** Ты написал длинную и некрасивую проверку на null.
    *   **Как исправить:** 
    *   *Было (примерно так):* `if (obj && obj.property && obj.property.value) { ... }`
    *   *Сделай так:* `if (obj?.property?.value) { ... }` (это называется optional chaining, SonarCloud очень его любит).

### 📁 6. `src/app/pages/games-page/games-page.component.html`
*   **Строка 166 (A11y BUG):** `Add a 'onKeyPress|onKeyDown|onKeyUp' attribute to this <tr> tag.`
    *   **Проблема:** Ошибка доступности (Accessibility). У тебя на теге строки таблицы `<tr>` висит обработчик клика мыши `(click)="doSomething()"`. По правилам хорошего тона (и для людей, не использующих мышь), если на элемент можно кликнуть, его можно должно быть активировать и с клавиатуры (Enter).
    *   **Как исправить:** Добавь обработчик нажатия клавиши и `tabindex`:
    *   *Стало:* `<tr (click)="doSomething()" (keydown.enter)="doSomething()" tabindex="0">`

### 📁 7. `src/main.ts`
*   **Строка 7:** `Prefer top-level await over using a promise chain.`
    *   **Проблема:** При старте Angular (с использованием standalone компонентов) обычно пишется цепочка промисов `.catch()`. Современный стандарт позволяет сделать это красивее через `await`.
    *   **Как исправить:**
    *   *Было:* 
      ```typescript
      bootstrapApplication(AppComponent, appConfig)
        .catch((err) => console.error(err));
      ```
    *   *Стало:* 
      ```typescript
      try {
        await bootstrapApplication(AppComponent, appConfig);
      } catch (err) {
        console.error(err);
      }
      ```

### 📁 8. `src/app/app.scss`
*   **Строка 1:** `Unexpected empty source`
    *   **Проблема:** Файл глобальных стилей пустой. SonarCloud не любит пустые файлы.
    *   **Как исправить:** Просто напиши на 1-й строке комментарий: `// Global application styles`

### 📁 9. `src/app/core/services/logger.service.ts`
*   **Строка 32:** `Extract this nested ternary operation into an independent statement.`
    *   **Проблема:** Вложенные тернарные операторы сложно читать.
    *   **Как исправить:** Перепиши на обычный `if / else if / else`.
    *   *Было (примерно):*
      ```typescript
      const level = isDebug ? 1 : isWarn ? 2 : 3;
      ```
    *   *Стало:*
      ```typescript
      let level = 3;
      if (isDebug) {
          level = 1;
      } else if (isWarn) {
          level = 2;
      }
      ```

---

**Итого:** 
1. Сначала почини два бага `TypeError` в React-модалках (добавь `?.`).
2. Объедини импорты и поправь `globalThis` в Angular.
3. Добавь нажатие с клавиатуры для строки таблицы.
Все эти 15 проблем реально исправить менее чем за 30-40 минут! Удачи!