# TableReady

Group 96 · Harrison, Kavin, Lehiem · CS 2114 Project 3, Deliverable 4 (Second Prototype + Tests)

TableReady is a waitlist for walk-in parties at a restaurant host stand. Parties are seated first-come, first-served from our own linked queue. For this deliverable we added our change feature, **no-shows**: when the party at the front doesn't answer, the host marks it, and it moves to the back of the line once. A second miss, or 15 minutes after the first miss, removes it.

The code is split into three packages:

- `tableReady.model`: `LinkedQueue<T>`, `Party` and `Waitlist` (the line and every waitlist rule), plus their tests
- `tableReady.view`: `TextView` (terminal screen) and `GuiView` (window)
- `tableReady.controller`: `WaitlistController` (text menu) and `GuiController` (window buttons), each with its own `main`, plus `WaitlistControllerTest`

## How to compile and run

**Setup:** Import the project into Eclipse. The tests extend `student.TestCase`, so the `CS2-Support` project must be in your workspace and on the build path (right-click the project → Build Path → Configure Build Path → Projects → Classpath → Add → CS2-Support).

**Compile:** Eclipse compiles the project every time you save, as long as Project → Build Automatically is checked. To compile the program from a terminal instead, run this from the project folder (the tests need CS2-Support, so they're left out here):

```
javac -d bin src/tableReady/model/LinkedQueue.java src/tableReady/model/Party.java src/tableReady/model/Waitlist.java src/tableReady/view/TextView.java src/tableReady/view/GuiView.java src/tableReady/controller/WaitlistController.java src/tableReady/controller/GuiController.java
```

Then run the text version with `java -cp bin tableReady.controller.WaitlistController` or the GUI with `java -cp bin tableReady.controller.GuiController`.

**Run the tests:** Right-click `src` → Run As → JUnit Test. All 136 tests pass:

| Test class | Tests | What it covers |
| --- | --- | --- |
| `LinkedQueueTest` | 28 | Every queue method, including order after interleaved enqueues and dequeues |
| `PartyTest` | 36 | Every `Party` method, including the no-show constructor and methods |
| `WaitlistTest` | 41 | Every `Waitlist` method, including `markNoShow` and `removeExpiredNoShows` with a fake clock |
| `WaitlistControllerTest` | 31 | Every bad-input case from our scope, plus the N command, fed in as typed text |

The three model test classes build only model objects. They never create a view, a `Scanner` or a controller.

**Run the text version:** Right-click `WaitlistController.java` → Run As → Java Application, then type commands in the Console:

| Key | Command |
| --- | --- |
| A | Add a party (name, then size 1–12) |
| V | View the line |
| P | Peek at who's next |
| S | Seat the next party |
| C | Change a waiting party's name or size (press Enter to keep a value) |
| R | Restart the night (asks y/n first) |
| N | Mark the front party as a no-show |
| E | End the night and quit |

Commands work in upper or lower case. Ending input (Ctrl+Z on Windows, Ctrl+D on Mac) also quits cleanly.

**Run the GUI:** Right-click `GuiController.java` → Run As → Java Application. Type a name and size and click **Add Party**, or use **Edit Party** (select a row first), **Seat Next**, **Peek Next**, **No-Show**, **Restart Night** and **End Night**. The floor plan on the right is a sample layout only; table occupancy isn't tracked.

## Reflection

### How did our design absorb the change?

No-shows touched six of our seven classes, every one of them where our D3 prediction said it would. `LinkedQueue` didn't change at all, and all 76 tests from Deliverable 3 still pass without edits.

| Class | Our D3 prediction | What actually changed | Matched? |
| --- | --- | --- | --- |
| `Party` (model) | New field `missedAt`, the 4-argument constructor, `hasMissed()` and `withMissedAt(long)` | All of those, plus the `arrivalTime` field and `getArrivalTime()` that the 4-argument constructor needs, and **`getMissedAt()`**, which we didn't predict. The 2-argument constructor now calls the 4-argument one with `0` and `-1`, and the new constructor also rejects negative times. | Yes, plus one extra method |
| `Waitlist` (model) | `Waitlist(LongSupplier clock)`, `Waitlist()` storing the real clock, `markNoShow()`, `removeExpiredNoShows()`, and `editParty()` keeping the miss mark | Exactly those | Yes |
| `WaitlistController` (controller) | `handleCommand()` gains N, a new `handleNoShow()`, and `run()` removes expired no-shows before each command | Exactly those | Yes |
| `TextView` (view) | Only `promptCommand()` changes; the no-show messages already existed | Only `promptCommand()` changed | Yes |
| `GuiController` (controller) | A new `handleNoShow()`, registered in `registerActions()`, and expired no-shows removed at the start of each button handler | Exactly those. The expiry check is one private method, `removeExpiredNoShows()`, that each button's listener calls first. | Yes |
| `GuiView` (view) | A No-show button and `onNoShow(ActionListener)` | Exactly those | Yes |
| `LinkedQueue` (model) | Should not change | Didn't change | Yes |

The methods we said should not change didn't change because of no-shows: `addParty`, `seatNext`, `peekNext`, `getParties`, `positionOf`, `restartNight` and `TextView.showWaitlist()`. `GuiView.refreshDisplay()` did change, but only for a D3 leftover ("1 party waiting" instead of "1 parties waiting"), not because of no-shows.

**What surprised us:**

- **`Party` needed `getMissedAt()`.** Our spec only had `hasMissed()`, but `removeExpiredNoShows()` has to know *when* a party missed to tell whether 15 minutes have passed, and `editParty()` has to copy the time over.
- **`TextView` needed almost nothing.** Its no-show messages (`showMovedToBack`, `showRemoved`, `showExpired`) were written in D3, so adding the feature to the text view was one menu line.
- **When to check for expired no-shows mattered.** We check *after* the host types a command and *before* it runs, not before the menu prints. Otherwise, a host who leaves the menu open for 20 minutes could seat a party that should already have been removed.

### Did the change cross the model/view line?

No. Neither view had to learn anything new about the model. `TextView` and `GuiView` still read only `getParties()`, `size()` and `getSeatedCount()`, and the no-show messages only use `Party.getName()`, which they already used. Nothing in `tableReady.model` imports from the view or controller packages. The new model logic (marking, requeuing, the 15-minute rule) lives only in `Party` and `Waitlist`, and both views get the right line just by redrawing from `getParties()`.

The change did ripple further than it needed to in one place. Because we have two controllers, the no-show logic had to be written twice: `handleNoShow()` and the expiry check exist in both `WaitlistController` and `GuiController`. That's duplication, not a boundary leak, but it's the cost we'd most like to remove (see the last part of this section).

### What changed from our D3 plan?

| Area | D3 plan | What we did | Why |
| --- | --- | --- | --- |
| Signatures | `Party` would have `hasMissed()` and `withMissedAt(long)` | Added `getMissedAt()` too | `removeExpiredNoShows()` and `editParty()` need the time of the miss, not just whether it happened |
| Features | Wait-time quotes committed as a stretch goal | Moved to the final build | To spend D4 on getting no-shows and its tests right |
| Signatures | The GUI would have its own copy of `parseSize` (a D3 leftover) | `GuiController` now calls `WaitlistController.parseSize`, and its copy is gone | The size rule's parsing now lives in one place |
| Validation | New bad input from the feature: marking a no-show when nobody is waiting | N and No-Show show "No one is waiting" / "There are no parties waiting"; `markNoShow()` throws `NoSuchElementException` if called anyway. `Party` also rejects negative arrival and miss times. | Matches how Seat and Peek already handle an empty line, and keeps a bad time from ever entering the model |
| Tests | `WaitlistControllerTest` was a D3 Next Steps item | Written: 31 tests, one per bad-input case from our scope plus the N command | It also closes scope unknown 4: feeding `TextView` a `Scanner` built from a `String` works |
| Tests | No interleaved queue test | `testInterleavedEnqueueDequeueKeepsFifoOrder` checks FIFO order across several rounds of enqueues and dequeues, then that the emptied queue rejects `dequeue` | Checks that `front` and `back` stay correct, not just simple add-all-then-remove-all order |

### What doesn't work yet

- Neither the GUI table nor the text list (V) shows which parties have already missed a call. The host only sees it in the message when it happens.
- Expired no-shows are removed when the host types a command or clicks a button, not on a timer. An expired party stays on the screen until the next action.
- The GUI has no automated tests. Its handlers are private and open pop-up windows, so we check it by clicking through every button. All its rules come from model methods and `WaitlistController.parseSize`, which are tested.
- Wait-time quotes aren't built yet (planned for the final build).

### What design change would make the next feature cheaper?

**A shared controller class.** Today, every feature is written twice: `WaitlistController` and `GuiController` each check the name, size, duplicates and the 50-party cap when adding, each have their own `handleNoShow()`, and each remove expired no-shows. If both controllers called one shared class, say `WaitlistActions`, with methods like `addParty(name, sizeText)` and `noShow()` that return a result (added, invalid size, duplicate, moved to back, removed...), each controller would only turn that result into its own message or pop-up. The next feature, wait quotes, would then be written and tested once instead of twice, and the GUI's logic would become testable without opening any windows.

## Next Steps

### What's left for the final build, in order

| # | Item | Owner |
| --- | --- | --- |
| 1 | Wait-time quotes in the model: `recentWaits` (a `LinkedQueue<Long>`), `addParty` stamping the arrival time, `seatNext` recording the wait, `restartNight` clearing the waits, and `quoteWaitMinutes()`, plus their `WaitlistTest` cases | Kavin |
| 2 | Show the quote after adding a party, in both views and controllers, plus `WaitlistControllerTest` cases | Lehiem |
| 3 | Mark missed parties in the text list and the GUI table, for example "Patel (missed)" | Lehiem |
| 4 | `PartyTest` and `LinkedQueueTest` review, and any `Party` changes quotes need | Harrison |
| 5 | If time allows: the shared controller class from the Reflection | Kavin, Lehiem |
| 6 | Final README, run every test, practice the demo | Everyone |

### Stretch goals

| Stretch goal | Decision |
| --- | --- |
| No-shows | Done (our change feature) |
| GUI screen | Done (built in Deliverable 3, extended with No-Show here) |
| Wait-time quotes | Committing, for the final build |

We aren't dropping any stretch goals from our scope.

### Our 3-minute demo

Timed in a practice run.

| Time | What we show |
| --- | --- |
| 0:00–0:20 | What TableReady is, and the green bar: all 136 tests passing |
| 0:20–0:55 | Text version: add Patel (4), Lee (2) and Kim (3), then V to view the line |
| 0:55–1:20 | **N**: Patel doesn't answer and moves to the back; V shows Lee, Kim, Patel |
| 1:20–1:40 | **N** twice more (Lee, Kim), then **N** on Patel's second miss: Patel is removed |
| 1:40–2:00 | Bad input: A with size "four", then 500, then 4; S and N on an empty line don't crash |
| 2:00–2:30 | Seat the remaining parties then end the night. |
| 2:30–3:00 | How the design absorbed the change: `LinkedQueue` untouched, `TextView` changed one line, the 15-minute rule proven by `testExpiredNoShowRemovedBeforeNextCommand` |

The 15-minute removal can't be shown live in 3 minutes, so we point to the test that checks it with a fake clock.

### Who does what

Owners are listed in the "What's left" table above. Kavin keeps this README up to date.

## Attribution

| File | Who | How |
| --- | --- | --- |
| `LinkedQueue.java` | Harrison, Kavin | D3: Kavin committed the GenAI-generated skeleton; Harrison wrote every method. D4: unchanged. |
| `Party.java` | Harrison, Kavin | D3: Harrison wrote it. D4: Kavin committed a GenAI-generated skeleton of the no-show methods so `Waitlist` could compile; Harrison replaced it with the real fields, 4-argument constructor and four methods. |
| `Waitlist.java` | Kavin | Kavin wrote it (D3) and its no-show methods (D4) from our spec. Claude reviewed and tested it. |
| `TextView.java` | Lehiem, Kavin | D3: Lehiem wrote it. D4: Kavin added the N menu line |
| `WaitlistController.java` | Lehiem, Kavin | D3: Lehiem wrote it. D4: Kavin added the N command, `handleNoShow()` and the expiry check in `run()` |
| `GuiView.java` | Lehiem, Kavin | D3: Lehiem wrote it. D4: Kavin added the No-Show button, `onNoShow()` and the "1 party" fix. |
| `GuiController.java` | Lehiem, Kavin | D3: Lehiem wrote it. D4: Kavin added `handleNoShow()`, the expiry check and the switch to `WaitlistController.parseSize`. |
| `LinkedQueueTest.java` | Harrison, Kavin | Harrison wrote all 28 tests, including the D4 interleaved-order test. Kavin fixed `testWorksWithLong` in D3 (GenAI-assisted). |
| `PartyTest.java` | Harrison | Harrison wrote all 36 tests, including the 16 no-show tests added in D4. |
| `WaitlistTest.java` | Kavin | Kavin wrote it (D3) and the 12 no-show tests (D4) from our test plan. |
| `WaitlistControllerTest.java` | Kavin | New in D4. Kavin wrote all 31 tests from our scope's bad-input cases. Claude reviewed and ran them. |


## GenAI note

We used Claude for this deliverable:

- **Planning:** we gave it our D3 README, our spec and the D4 instructions and rubric, and made sure we were doing the correct work we had left like said in the previous deliverable's READme.
- **Code:** it helped with the D4 no-show code in `Waitlist`, the N command and No-Show button in both controllers and views, and the two D3 leftovers, checking with our prediction in our D3 Next Steps. It also generated the `Party` skeleton Kavin committed so `Waitlist` could compile; Harrison wrote the real `Party` code.
- **Tests:** it helped write and check the 12 new `WaitlistTest` cases and all of `WaitlistControllerTest`.
- **Checking:** it compiled the project for Java 8 and ran every test with a stand-in for `student.TestCase` that rejects numbers passed as objects, the way CS2-Support does. It broke the new `Waitlist` and controller code on purpose in 16 ways, and the tests caught every one. It also checked Harrison's `Party` against what `Waitlist` calls and broke it in 8 ways; his `PartyTest` caught every one. It ran the text version through the MVP path, no-shows and every bad-input case, and clicked through the GUI on a virtual screen. Kavin then ran all 136 tests in Eclipse with the real CS2-Support library and got a green bar.
- **This README:** it checked over the README from our code, our D3 plan and the rubric, after Kavin made it.
