"""
Generuje PNG z diagramem maszyny stanow wniosku (V7).
Uruchomienie: python generate_state_diagram.py
Wynik: maszyna_stanow.png w tym samym katalogu.
"""
import matplotlib.pyplot as plt
import matplotlib.patches as patches
from matplotlib.patches import FancyBboxPatch, FancyArrowPatch
from matplotlib.lines import Line2D

BG       = "#1f1f1f"
BOX_BG   = "#2b2b2b"
BOX_EDGE = "#7d7d7d"
TEXT     = "#ffffff"
SUBTEXT  = "#bdbdbd"
ROLE_CLR = "#9e9e9e"
ARROW    = "#bdbdbd"

GREEN  = "#1faa59"   # ACCEPTED
RED    = "#e0524b"   # REJECTED
YELLOW = "#e6b339"   # CORRECTION_REQUIRED

fig, ax = plt.subplots(figsize=(15, 7.5), dpi=160)
fig.patch.set_facecolor(BG)
ax.set_facecolor(BG)
ax.set_xlim(0, 15)
ax.set_ylim(0, 7.5)
ax.axis("off")


def draw_box(x, y, w, h, title, subtitle, role, edge=BOX_EDGE, title_clr=TEXT):
    box = FancyBboxPatch(
        (x, y), w, h,
        boxstyle="round,pad=0.02,rounding_size=0.15",
        linewidth=1.5, edgecolor=edge, facecolor=BOX_BG,
    )
    ax.add_patch(box)
    cx = x + w / 2
    ax.text(cx, y + h - 0.42, title, ha="center", va="center",
            color=title_clr, fontsize=12, fontweight="bold")
    ax.text(cx, y + h / 2 - 0.05, subtitle, ha="center", va="center",
            color=SUBTEXT, fontsize=9.5)
    ax.text(cx, y + 0.30, role, ha="center", va="center",
            color=ROLE_CLR, fontsize=8.5, style="italic")
    return (cx, y, x, y + h, x + w)  # cx, bottom, left, top, right


# ---- TOP ROW: szczesliwa sciezka ---------------------------------------
top_y = 4.3
box_w, box_h = 2.5, 1.9
gap = 0.55
start_x = 0.25

boxes = []
labels = [
    ("DRAFT", "Wersja robocza", "ROLE_STUDENT", BOX_EDGE, TEXT),
    ("SUBMITTED", "Złożony", "", BOX_EDGE, TEXT),
    ("FORMAL_EVAL.", "Ocena formalna", "ROLE_DEAN_OFFICE", BOX_EDGE, TEXT),
    ("UNDER_REVIEW", "Ocena merytoryczna", "WRSS / LEGAL_COMMISSION", BOX_EDGE, TEXT),
    ("ACCEPTED", "Przyznano środki", "ROLE_ADMIN (Rektorat)", GREEN, GREEN),
]
for i, (t, s, r, edge, tclr) in enumerate(labels):
    x = start_x + i * (box_w + gap)
    info = draw_box(x, top_y, box_w, box_h, t, s, r, edge=edge, title_clr=tclr)
    boxes.append(info)

# strzalki miedzy gornymi pudelkami + etykiety nad nimi
transitions = [
    "Koordynator składa",
    "CSSDiR · formalna",
    "Samorząd / komisja",
    "Rektor · decyzja",
]
for i in range(len(boxes) - 1):
    left = boxes[i][4]      # right edge of box i
    right = boxes[i + 1][2] # left edge of box i+1
    yc = top_y + box_h / 2
    arrow = FancyArrowPatch(
        (left + 0.05, yc), (right - 0.05, yc),
        arrowstyle="-|>", mutation_scale=14,
        linewidth=1.6, color=ARROW,
    )
    ax.add_patch(arrow)
    midx = (left + right) / 2
    ax.text(midx, yc + 1.15, transitions[i], ha="center", va="center",
            color=SUBTEXT, fontsize=9)

# ---- BOTTOM ROW: REJECTED i CORRECTION_REQUIRED -----------------------
bot_y = 1.3
bot_w, bot_h = 2.3, 1.5

# REJECTED pod FORMAL_EVAL.
rej_cx = boxes[2][0]
rej = draw_box(rej_cx - bot_w / 2, bot_y, bot_w, bot_h,
               "REJECTED", "Odrzucony", "", edge=RED, title_clr=RED)

# CORRECTION_REQ. pod UNDER_REVIEW
cor_cx = boxes[3][0]
cor = draw_box(cor_cx - bot_w / 2, bot_y, bot_w, bot_h,
               "CORRECTION_REQ.", "Do poprawy", "", edge=YELLOW, title_clr=YELLOW)

# ---- Czerwone strzalki: z kazdego etapu oceny -> REJECTED -------------
for src_idx in [1, 2, 3]:  # SUBMITTED, FORMAL_EVAL, UNDER_REVIEW
    sx = boxes[src_idx][0]
    sy = top_y  # bottom of top boxes
    arrow = FancyArrowPatch(
        (sx, sy - 0.05), (rej[0] + (sx - rej[0]) * 0.05, bot_y + bot_h + 0.05),
        arrowstyle="-|>", mutation_scale=12,
        linewidth=1.3, color=RED, linestyle=(0, (4, 3)),
        connectionstyle="arc3,rad=0.0",
    )
    # prostsza wersja: prosto w dol z lekkim skosem do REJECTED
    arrow = FancyArrowPatch(
        (sx, sy - 0.05),
        (rej[0], bot_y + bot_h + 0.05),
        arrowstyle="-|>", mutation_scale=12,
        linewidth=1.3, color=RED, linestyle=(0, (4, 3)),
    )
    ax.add_patch(arrow)

# ---- Zolte strzalki: z kazdego etapu oceny -> CORRECTION_REQ ---------
for src_idx in [1, 2, 3]:
    sx = boxes[src_idx][0]
    sy = top_y
    arrow = FancyArrowPatch(
        (sx, sy - 0.05),
        (cor[0], bot_y + bot_h + 0.05),
        arrowstyle="-|>", mutation_scale=12,
        linewidth=1.3, color=YELLOW, linestyle=(0, (4, 3)),
    )
    ax.add_patch(arrow)

# ---- Etykieta nad strzalkami w dol (z tlem zeby zaslonic strzalki) ---
ax.text(7.6, 3.55,
        "Z każdego etapu oceny\n(SUBMITTED / FORMAL / REVIEW):",
        ha="center", va="center", color=SUBTEXT, fontsize=9.5,
        bbox=dict(boxstyle="round,pad=0.35", facecolor=BG,
                  edgecolor="none"))

# ---- Petla CORRECTION_REQ -> DRAFT -----------------------------------
draft_cx, draft_bot, draft_left, draft_top, draft_right = boxes[0]
arrow = FancyArrowPatch(
    (cor[2], bot_y + bot_h / 2),                 # left side of correction box
    (draft_cx, draft_bot - 0.05),                # bottom of DRAFT
    arrowstyle="-|>", mutation_scale=12,
    linewidth=1.3, color=YELLOW, linestyle=(0, (4, 3)),
    connectionstyle="arc3,rad=-0.25",
)
ax.add_patch(arrow)
ax.text(2.8, 1.95, "poprawiony wraca do DRAFT",
        ha="center", va="center", color=YELLOW, fontsize=8.5, style="italic")

# ---- LEGENDA --------------------------------------------------------
legend_y = 0.45
items = [
    (BOX_EDGE,  "etap procesu"),
    (GREEN,     "sukces"),
    (RED,       "odrzucenie"),
    (YELLOW,    "poprawa"),
]
lx = 0.4
for clr, label in items:
    sq = FancyBboxPatch(
        (lx, legend_y - 0.18), 0.35, 0.35,
        boxstyle="round,pad=0.02,rounding_size=0.05",
        linewidth=1.2, edgecolor=clr, facecolor=BOX_BG,
    )
    ax.add_patch(sq)
    ax.text(lx + 0.5, legend_y, label, ha="left", va="center",
            color=SUBTEXT, fontsize=9)
    lx += 1.7

ax.text(lx + 0.2, legend_y,
        "— mapowanie ról to propozycja do potwierdzenia",
        ha="left", va="center", color=SUBTEXT, fontsize=9, style="italic")

plt.tight_layout(pad=0.3)
out_path = "maszyna_stanow.png"
plt.savefig(out_path, dpi=160, facecolor=BG, bbox_inches="tight", pad_inches=0.2)
print(f"Zapisano: {out_path}")
