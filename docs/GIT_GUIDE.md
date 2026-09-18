# Git and GitHub Guide for ClubGear

Version control is worth 10% of the evaluation, and an empty history with one commit called
"final upload" scores badly. This page explains how to put ClubGear on GitHub with a history
that looks like real development work.

---

## 1. One-time setup

```bash
git config --global user.name "Your Name"
git config --global user.email "your.email@college.edu"
```

Create an empty repository on GitHub named `clubgear` (do **not** tick "Add a README",
because this project already has one).

---

## 2. Initialise the local repository

From inside the `ClubGear` folder:

```bash
git init
git branch -M main
git remote add origin https://github.com/YOUR-USERNAME/clubgear.git
```

---

## 3. Suggested commit sequence

Commit in the order the project was actually built, not all at once. Each command below is
one commit. Stage only the files named in that step.

| # | Command | Commit message |
|---|---------|----------------|
| 1 | `git add .gitignore README.md statement.md` | `docs: add problem statement and initial README` |
| 2 | `git add src/clubgear/BookingStatus.java src/clubgear/EquipmentStatus.java` | `feat: add BookingStatus and EquipmentStatus enums` |
| 3 | `git add src/clubgear/Club.java src/clubgear/Member.java` | `feat: add Club and Member model classes with CSV conversion` |
| 4 | `git add src/clubgear/Equipment.java src/clubgear/MaintenanceRecord.java` | `feat: add Equipment and MaintenanceRecord models` |
| 5 | `git add src/clubgear/Booking.java` | `feat: add Booking model with date overlap detection` |
| 6 | `git add src/clubgear/InputValidator.java` | `feat: add InputValidator for safe keyboard input` |
| 7 | `git add src/clubgear/FileStorage.java` | `feat: add CSV file storage layer` |
| 8 | `git add src/clubgear/ClubGearSystem.java src/clubgear/Main.java` | `feat: add menu system and all booking operations` |
| 9 | `git add tests/` | `test: add validation test suite and manual test plan` |
| 10 | `git add docs/DESIGN.md` | `docs: add architecture, UML and storage design` |
| 11 | `git add docs/SCREENSHOTS.md docs/GIT_GUIDE.md` | `docs: add sample run output and git guide` |
| 12 | `git add .` | `docs: add project report PDF` |

Each commit is made with:

```bash
git commit -m "the message from the table"
```

Then push everything:

```bash
git push -u origin main
```

---

## 4. Good commit message style

The table above uses the **conventional commits** prefix style, which is what most teams use:

| Prefix | Meaning |
|--------|---------|
| `feat:` | a new feature |
| `fix:` | a bug fix |
| `docs:` | documentation only |
| `test:` | tests only |
| `refactor:` | code restructuring with no behaviour change |
| `chore:` | build files, .gitignore, housekeeping |

Write the message in the imperative ("add booking validation", not "added booking
validation") and keep the first line under about 60 characters.

---

## 5. Showing a branch in the history

Evaluators like to see that branching is understood. One honest way to show it:

```bash
git checkout -b feature/maintenance-module
# ... make a small improvement to the maintenance module ...
git add src/clubgear/ClubGearSystem.java
git commit -m "feat: add maintenance history filter by equipment"
git checkout main
git merge feature/maintenance-module
git push
```

Only do this if you really make the change. A fake branch is easy to spot in the diff.

---

## 6. What must NOT be committed

The `.gitignore` in this repository already excludes:

- `out/` and `out-tests/` — compiled `.class` files, which are build output, not source
- `data/` — runtime data created by the program; the repository should ship with no saved
  state so that a fresh clone always starts with the sample data
- IDE folders (`.idea/`, `.vscode/`, `.settings/`) and OS junk (`.DS_Store`, `Thumbs.db`)

Committing `.class` files is the most common mistake in student repositories and it looks
careless, because the grader then has to work out which files are yours.

---

## 7. Checklist before the deadline

- [ ] `git status` reports a clean working tree
- [ ] `git log --oneline` shows at least 10 meaningful commits
- [ ] The repository has no `out/`, `out-tests/` or `data/` folder
- [ ] `README.md` renders correctly on the GitHub page
- [ ] The Mermaid diagrams in `docs/DESIGN.md` render on GitHub
- [ ] A fresh `git clone` compiles with `javac -d out src/clubgear/*.java`
- [ ] The report PDF is uploaded separately on the portal as well
