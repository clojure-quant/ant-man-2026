import { GoldenLayout } from "https://cdn.jsdelivr.net/npm/golden-layout@2.6.0/+esm"

let layoutInstance = null
let resizeHandler = null
const panelContainers = {}

const LAYOUT_CONFIG = {
  root: {
    type: "column",
    content: [
      {
        type: "component",
        componentType: "memo",
        title: "Memo",
        size: "15%",
        minSize: "80px",
      },
      {
        type: "row",
        content: [
          {
            type: "component",
            componentType: "positions",
            title: "Positions",
            size: "50%",
            minSize: "120px",
          },
          {
            type: "component",
            componentType: "trades",
            title: "Trades",
            size: "50%",
            minSize: "120px",
          },
        ],
      },
    ],
  },
}

function fillContainer(container, child) {
  const parent = container.element
  parent.style.position = "relative"
  parent.style.height = "100%"
  parent.style.width = "100%"
  parent.style.margin = "0"
  parent.style.padding = "0"
  parent.style.overflow = "hidden"
  parent.style.minHeight = "0"
  parent.appendChild(child)
}

function attachStreamPanel(panelElId, containerKey) {
  const el = document.getElementById(panelElId)
  const container = panelContainers[containerKey]
  if (!el || !container) return
  if (!container.contains(el)) {
    container.innerHTML = ""
    fillContainer({ element: container }, el)
  }
}

function attachStreamPanels() {
  attachStreamPanel("panel-positions", "positions")
  attachStreamPanel("panel-trades", "trades")
}

function registerComponents(layout) {
  layout.registerComponentFactoryFunction("memo", (container) => {
    const el = document.createElement("div")
    el.className = "memo-panel"
    el.innerHTML = "<p>welcome to ant-man-trading</p>"
    fillContainer(container, el)
  })

  layout.registerComponentFactoryFunction("positions", (container) => {
    panelContainers.positions = container.element
    attachStreamPanel("panel-positions", "positions")
  })

  layout.registerComponentFactoryFunction("trades", (container) => {
    panelContainers.trades = container.element
    attachStreamPanel("panel-trades", "trades")
  })
}

function ensureMount(host) {
  let mount = host.querySelector("#gl-mount")
  if (!mount) {
    mount = document.createElement("div")
    mount.id = "gl-mount"
    mount.style.height = "100%"
    mount.style.width = "100%"
    host.appendChild(mount)
  }
  return mount
}

function destroyLayout() {
  const host = document.getElementById("golden-layout-host")
  if (host) delete host.dataset.glInit
  if (resizeHandler) {
    window.removeEventListener("resize", resizeHandler)
    resizeHandler = null
  }
  if (layoutInstance) {
    try {
      layoutInstance.destroy()
    } catch (_) {
      /* already torn down */
    }
    layoutInstance = null
  }
  panelContainers.positions = null
  panelContainers.trades = null
}

function resizeLayout(host) {
  if (!layoutInstance || !host) return
  layoutInstance.setSize(host.clientWidth, host.clientHeight)
}

export function initLayout() {
  const host = document.getElementById("golden-layout-host")
  if (!host) return false

  destroyLayout()
  const mount = ensureMount(host)
  mount.innerHTML = ""

  const layout = new GoldenLayout(LAYOUT_CONFIG, mount)
  registerComponents(layout)
  layout.init()
  layoutInstance = layout

  attachStreamPanels()

  resizeHandler = () => resizeLayout(host)
  window.addEventListener("resize", resizeHandler)
  resizeLayout(host)

  host.dataset.glInit = "true"
  return true
}

window.antmanInitLayout = initLayout
window.antmanAttachPanels = attachStreamPanels

function tryInit() {
  const host = document.getElementById("golden-layout-host")
  if (!host || host.dataset.glInit === "true") return
  initLayout()
}

function watchForLayoutHost() {
  const root = document.getElementById("hyper-app") || document.body
  const observer = new MutationObserver(() => {
    const host = document.getElementById("golden-layout-host")
    if (!host) {
      if (layoutInstance) destroyLayout()
      return
    }
    if (host.dataset.glInit !== "true") {
      tryInit()
    } else if (panelContainers.positions) {
      attachStreamPanels()
    }
  })
  observer.observe(root, { childList: true, subtree: true })
}

if (document.readyState === "loading") {
  document.addEventListener("DOMContentLoaded", () => {
    tryInit()
    watchForLayoutHost()
  })
} else {
  tryInit()
  watchForLayoutHost()
}
