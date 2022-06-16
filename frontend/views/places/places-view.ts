import '@vaadin/vaadin-ordered-layout/vaadin-horizontal-layout';
import '@vaadin/vaadin-ordered-layout/vaadin-vertical-layout';
import '@vaadin/vaadin-select';
import { applyTheme } from 'Frontend/generated/theme';
import { customElement, html, LitElement } from 'lit-element';

@customElement('places-view')
export class PlacesView extends LitElement {
  connectedCallback() {
    super.connectedCallback();
    applyTheme(this.renderRoot);
  }

  render() {
    return html`
      <main class="max-w-screen-lg mx-auto pb-l px-l">
        <vaadin-horizontal-layout class="items-center justify-between">
          <vaadin-vertical-layout>
            <h2 class="mb-0 mt-xl text-3xl">Miejsca</h2>
            <p class="mb-xl mt-0 text-secondary">Zobacz gdzie odbywają się zawody!</p>
          </vaadin-vertical-layout>
        </vaadin-horizontal-layout>
        <ol class="gap-m grid list-none m-0 p-0">
          <slot></slot>
        </ol>
      </main>
    `;
  }
}
