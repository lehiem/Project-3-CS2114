# TableReady

Group 96 · Harrison, Kavin, Lehiem · CS 2114 Project 3, Deliverable 3 (First Prototype)

TableReady is a waitlist for walk-in parties at a restaurant host stand. Parties are seated first-come, first-served from our own linked queue. The code is split into three packages:

- `tableReady.model`: `LinkedQueue<T>`, `Party` and `Waitlist` (the line and every waitlist rule)
- `tableReady.view`: `TextView` (terminal screen) and `GuiView` (window)
- `tableReady.controller`: `WaitlistController` (text menu) and `GuiController` (window buttons); each has its own `main`

## How to run

**Setup:** Import the project into Eclipse. The tests extend `student.TestCase`, so the `CS2-Support` project must be in your workspace and on the build path (right-click the project → Build Path → Configure Build Path → Projects → Classpath → Add → CS2-Support).

**Run the model (no view needed):** Right-click `src` → Run As → JUnit Test to run every test, or right-click one file, such as `WaitlistTest.java`, → Run As → JUnit Test. The model tests build only `LinkedQueue`, `Party` and `Waitlist`. They never create a view, a `Scanner` or a controller. All 76 model tests pass: `LinkedQueueTest` (27), `PartyTest` (20) and `WaitlistTest` (29).

**Run the thin view (text):** Right-click `WaitlistController.java` → Run As → Java Application, then type commands in the Console:

| Key | Command |
| --- | --- |
| A | Add a party (name, then size 1–12) |
| V | View the line |
| P | Peek at who's next |
| S | Seat the next party |
| C | Change a waiting party's name or size (press Enter to keep a value) |
| R | Restart the night (asks y/n first) |
| E | End the night and quit |

Commands work in upper or lower case. Ending input (Ctrl+Z on Windows, Ctrl+D on Mac) also quits cleanly.

**Run the GUI (stretch goal):** Right-click `GuiController.java` → Run As → Java Application. Type a name and size and click **Add Party**, or use **Edit Party** (select a row first), **Seat Next**, **Peek Next**, **Restart Night** and **End Night**. The floor plan on the right is a sample layout only; table occupancy isn't tracked.

## Reflection

### What changed from our specification

| Area | Spec said | What we built | Why | What prompted it |
| --- | --- | --- | --- | --- |
| Classes: packages | Packages `model`, `view` and `controller` | `tableReady.model`, `tableReady.view` and `tableReady.controller` | Our Eclipse project already used a `tableReady` package, so we split it into three subpackages. Three packages make the model/view line easy to check: nothing in `tableReady.model` imports from the other two. | Project setup. We considered keeping one package and chose three after asking GenAI which was better. |
| Classes: build order | Each member builds their classes on their own branch | We first committed skeletons of `LinkedQueue` and `Party`, with the spec's exact signatures and empty method bodies, so `Waitlist` could compile before they were written | Without them, `Waitlist` couldn't compile, and nobody could build on the model until Harrison was done | 19 "is undefined" compile errors in `Waitlist.java` |
| Classes: GUI built early | The GUI (`GuiView`, `GuiController`) was a stretch goal for after the MVP, and we planned to drop it | Both GUI classes are built now, next to the text version. `TextView` also already has the no-show and wait-quote messages from our stretch goals. | A second view running on the same, unchanged `Waitlist` shows the model doesn't depend on any one view | The text version was finished ahead of schedule, so Lehiem started the GUI stretch goal early |
| Signatures: GUI wiring | `GuiView.setController(GuiController)`; public `GuiController.onAdd(String, String)`, `onChange`, `onSeat`, `onPeek` and `onRestart`; `GuiView` has the same `show...` methods as `TextView`, and the GUI reuses `WaitlistController.parseSize` | `GuiView` has `onAdd(ActionListener)`, `onEdit`, `onSeat`, `onPeek`, `onRestart` and `onExit`, which the controller uses to register private handlers. Messages go through `showMessage`, `showError` and `refreshDisplay`. Seating asks for a Yes/No first, there's an End Night button, and `GuiController` has its own copy of `parseSize`. | Registering listeners keeps every Swing button inside `GuiView`, so `GuiController` never touches a `JButton` | While building the GUI, Lehiem found that Swing buttons take an `ActionListener`, so registering listeners fit Swing better than the spec's `onAdd(String, String)` plan |
| Signatures: GUI pop-ups | Every window belongs to the view | `GuiView` gained `promptNewName(String)`, `promptNewSize(int)` and `confirmSeat(Party)`. Our first version had `GuiController` opening those three pop-ups itself with `JOptionPane`. | Opening windows and reading input is view work, so with the pop-ups inside `GuiController`, it spanned two MVC roles. Our spec says no class does that. | A GenAI review of the GUI code |
| Tests: setup | Test classes `LinkedQueueTest`, `PartyTest` and `WaitlistTest`; no location given | Test classes sit in `tableReady.model` next to the classes they test and extend `student.TestCase` | Same setup as our labs and Project 2, and the same package means no extra imports | 70 "student cannot be resolved" errors until we added CS2-Support to the build path |
| Tests: cases | One normal and one bad-input case per public method | 76 tests in all. `WaitlistTest` (29) adds checks that `editParty` keeps everyone's place, that `getParties()` returns a copy, that adding works after the line empties, and that two waitlists don't share a line. `LinkedQueueTest` (27) adds a check that the queue also works as a `LinkedQueue<Long>`. | The copy test protects our model/view boundary, and the empty-then-add tests in `WaitlistTest` and `LinkedQueueTest` cover scope unknown 1. The `Long` test covers the wait-quote stretch goal, which reuses `LinkedQueue` for wait times. | For `WaitlistTest`, a GenAI review of our spec, which pointed out that the test plan never tested a stale `back` reference. For `LinkedQueueTest`, our spec's class design, which says the wait-quote stretch goal reuses `LinkedQueue<Long>`. |

**Unchanged:** `LinkedQueue`, `Party`, `Waitlist`, `TextView` and `WaitlistController` match the spec's Method signatures section, and the text version handles every case in the spec's Where validation lives table: mistyped and blank commands; blank, all-space and 5,000-character names; sizes like "four", 0, 500 and 99999999999; duplicates like "patel " after "Patel"; seating, peeking or changing with nobody waiting; renaming to another party's name; the y/n restart check; the 50-party cap; and Ctrl+D at any prompt. None of them crash.

### Did the model really stand alone?

Yes. Nothing in `tableReady.model` imports anything from `tableReady.view` or `tableReady.controller`; the only import is Java's `NoSuchElementException`. `Waitlist` uses only `LinkedQueue` and `Party`, and the model tests never create a view, a `Scanner` or a controller. Nothing had to move across the model/view line to make the model run, and both views run on the same `Waitlist` with no model changes. The only thing that crossed the wrong way was in the GUI: the first `GuiController` opened its own pop-ups, so we moved them into `GuiView` (see the GUI pop-ups row above).

### What happened to our unknowns

| Scope unknown | Status | What we found |
| --- | --- | --- |
| 1. How does `dequeue` reset the `back` reference when the last party leaves? | Figured out | When `dequeue` removes the last node, it sets `back` to `null` as well as `front`. Two passing tests check it: `LinkedQueueTest.testEnqueueAfterQueueEmptiedAgain` and `WaitlistTest.testAddAfterLineEmpties`. Each empties the line, adds again, and checks that the new item is next. |
| 2. Should the model give the view a copy of the line? | Figured out | Yes. `Waitlist.getParties()` returns a new `Party[]`, and a `Party` can't be changed after it's made, so neither view can alter the real line. `TextView.showWaitlist()` and `GuiView.refreshDisplay()` both read the line this way, and `testGetPartiesReturnsCopy` checks it. |
| 3. Should `dequeue` on an empty line throw or return `null`, and who handles it? | Figured out | It throws `NoSuchElementException`, and so do `Waitlist.seatNext()` and `peekNext()`; our tests check all three. Both controllers check `isEmpty()` first, so seating or peeking with nobody waiting shows a "No one is waiting" message instead of crashing. |
| 4. How do we JUnit-test `Scanner` input without typing? | Still open | `TextView` takes its `Scanner` and `PrintStream` in its constructor, so a test can pass in `new Scanner("A\nPatel\n4\nE\n")`, and running the program with typed input piped in works. We haven't written `WaitlistControllerTest` yet, so it stays open until that test runs. |

### What doesn't work yet

- There's no `WaitlistControllerTest`, so the text menu and both controllers are only checked by running them, not by JUnit tests.
- The GUI has no automated tests; we check it by clicking through every button.
- The GUI's count says "1 parties waiting" when one party is waiting.
- `GuiController` has its own copy of `parseSize` instead of reusing `WaitlistController.parseSize`, so the size rule's parsing lives in two places.
- Our change feature and stretch goals (no-shows and wait-time quotes) aren't built yet; they're planned for Deliverable 4 below.

## Next Steps

### What's left to finish the MVP, in order

Every MVP feature works in both views. What's left is testing and small fixes:

| # | Item | Owner |
| --- | --- | --- |
| 1 | Write `WaitlistControllerTest`, feeding the controller a `Scanner` built from a `String`, to close scope unknown 4 | Lehiem |
| 2 | Make `GuiController` call `WaitlistController.parseSize` and delete its own copy | Lehiem |
| 3 | Fix the GUI count to say "1 party waiting" | Lehiem |
| 4 | Click through every GUI button and every bad-input case once more after the fixes | Everyone |

### Our change feature: no-shows

When the front party doesn't answer, the host picks **N** in the text version or clicks a **No-show** button in the GUI. That party moves to the back of the line once. A second miss removes it right away, and a party that still hasn't been seated 15 minutes after its miss is removed too. This adds model state (when a party missed its call) and model logic (requeue, then remove), and the host reaches it through both controllers and views.

**Prediction: what it will touch**

- `Party` (model): a new field `missedAt`; the spec's new constructor `Party(String name, int size, long arrivalTime, long missedAt)`, shared with wait quotes; and new methods `hasMissed()` and `withMissedAt(long time)`.
- `Waitlist` (model): a new constructor `Waitlist(LongSupplier clock)` so tests can fake the time; `Waitlist()` stores the real clock; new methods `markNoShow()` and `removeExpiredNoShows()`; and a change to `editParty()` so a changed party keeps its miss mark.
- `WaitlistController` (controller): `handleCommand()` gains **N**, a new `handleNoShow()` handles it, and `run()` calls `removeExpiredNoShows()` before each command.
- `TextView` (view): only `promptCommand()` changes, to list **N** again. `showMovedToBack(Party)`, `showRemoved(Party)` and `showExpired(int)` already exist.
- `GuiController` (controller): a new private `handleNoShow()`, registered in `registerActions()`, and a call to `removeExpiredNoShows()` at the start of each button handler.
- `GuiView` (view): a new **No-show** button and a new `onNoShow(ActionListener)` method.

**What our design says should not change**

- `LinkedQueue`: no-shows only need `enqueue` and `dequeue`, which already exist.
- The other `Waitlist` methods (`addParty`, `seatNext`, `peekNext`, `getParties`, `positionOf`, `restartNight`) and every existing method signature. All 76 current tests should keep passing untouched.
- `TextView.showWaitlist()` and `GuiView.refreshDisplay()`: both already redraw the line from `getParties()`, so a party moved to the back shows up correctly with no changes.

### Stretch goals

| Stretch goal | Decision | Why |
| --- | --- | --- |
| No-shows | Committing | It's our change feature |
| Wait-time quotes | Committing | It's small. It reuses our `LinkedQueue` as `LinkedQueue<Long>` for the last 5 waits, and needs `Party.arrivalTime`, `Waitlist.quoteWaitMinutes()`, and changes to `addParty`, `seatNext` and `restartNight`. `TextView.showQuote(int)` already exists; the GUI's add message needs the quote added. |
| GUI screen | Already built | Built in this prototype (see Reflection). We'll keep it working as we add the other two goals. |

We aren't dropping any of the stretch goals from our scope.

### New tests we'll need

| Test file | New cases |
| --- | --- |
| `PartyTest` | `hasMissed()` is false for a new party. `withMissedAt(5000)` returns a marked copy with the same name and size, and the original party is unchanged. |
| `WaitlistTest` | `markNoShow()`: the first miss moves the party to the back; a second miss removes it; an empty waitlist throws `NoSuchElementException`. `removeExpiredNoShows()` with a fake clock: removes a party at 15 minutes, keeps it at 14, and returns 0 when empty. `editParty()` keeps a party's miss mark. `quoteWaitMinutes()`: waits of 10, 20 and 30 minutes give 20; waits of 12 and 14 give 15 (rounded); -1 before anyone is seated and after `restartNight()`; only the last 5 waits count. |
| `WaitlistControllerTest` | The MVP commands and bad-input cases from item 1 above. **N** with nobody waiting shows "No one is waiting." **N** moves the front party to the back. Adding a party shows a quote, or "No estimate yet." before anyone is seated. |

### Who does what

| Item | Owner |
| --- | --- |
| `Party` no-show and quote fields and methods, plus their `PartyTest` cases | Harrison |
| `Waitlist` clock constructor, `markNoShow`, `removeExpiredNoShows`, `quoteWaitMinutes`, and the `editParty`, `addParty`, `seatNext` and `restartNight` changes, plus their `WaitlistTest` cases | Kavin |
| The **N** command and No-show button in both controllers and views, the quote message after adding, plus `WaitlistControllerTest` | Lehiem |
| Keeping this README up to date | Everyone |

## Attribution

| File | Who | How |
| --- | --- | --- |
| `LinkedQueue.java` | Harrison, Kavin | Kavin committed the skeleton (signatures and empty methods). Harrison wrote every method and its Javadoc. |
| `Party.java` | Harrison, Kavin | Kavin committed the skeleton. Harrison wrote every method and its Javadoc. |
| `Waitlist.java` | Kavin | Kavin wrote it from our spec's method signatures. Kavin reviewed to the spec and ran `WaitlistTest` on Harrison's `LinkedQueue` and `Party`; all 29 tests pass. |
| `TextView.java` | Lehiem, Kavin | Lehiem wrote it. Kavin removed the "N - Mark no-show" menu line, since **N** isn't built yet. |
| `WaitlistController.java` | Lehiem, Kavin | Lehiem wrote it. Kavin replaced the template class comment with a real summary. |
| `GuiView.java` | Lehiem, Kavin | Lehiem wrote it. Kavin added `promptNewName`, `promptNewSize` and `confirmSeat` to move the pop-ups out of `GuiController`. |
| `GuiController.java` | Lehiem, Kavin | Lehiem wrote it. Kavin changed its three pop-up calls to use the new `GuiView` methods and removed its `JOptionPane` import. |
| `LinkedQueueTest.java` | Harrison | Wrote all 27 tests. Kavin reviewed it and fixed one test. |
| `PartyTest.java` | Harrison | Wrote all 20 tests |
| `WaitlistTest.java` | Kavin | Kavin wrote it from our spec's test plan. Kavin reviewed it with the spec and ran it. |

## GenAI note

We used Claude for this prototype:

- **Planning:** we gave it our spec and the Deliverable 3 instructions and rubric, and asked if our plan to build and division of work would work properly and made sense.
- **Project setup:** it walked us through adding a `.gitignore` so compiled `bin/` files stay out of GitHub.
- **Skeletons:** it helped make sure compile-ready skeletons of the MVP classes with our spec's exact signatures and empty methods. We committed the `LinkedQueue` and `Party` skeletons so `Waitlist` could compile before Harrison's versions were ready.
- **`Waitlist` and `WaitlistTest`:** it checked them by running the tests against working stand-in versions of `LinkedQueue` and `Party`, which we did not commit, and against `Waitlist` with bugs added on purpose, which the tests caught. Kavin reviewed both files and ran `WaitlistTest` on Harrison's code; all 29 tests pass.
- **Checking Harrison's code:** it compared `LinkedQueue` and `Party` with our spec and ran all three test classes together. It also broke `LinkedQueue` and `Party` on purpose in nine ways, such as a stale `back` reference, a missing `size--` and a name that isn't trimmed, to check that `LinkedQueueTest` and `PartyTest` catch real bugs. They caught all nine.
- **Checking Lehiem's code:** it compiled the whole project and ran the text version through the MVP path and every bad-input case from our scope using typed input, and nothing crashed. It found the unbuilt **N** command on the menu and the template class comment, which Kavin fixed. It also found that `GuiController` opened three pop-ups itself, and wrote the fix that moved them into `GuiView`, which Kavin applied.
