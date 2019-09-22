import { NgModule, Component } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { PromotorComponent} from "./promotor.component"
import { ClientesComponent } from './clientes/clientes.component'
import { PreCfdiComponent } from './pre-cfdi/pre-cfdi.component'
import { ReportesComponent } from './reportes/reportes.component'
import { PagosComponent } from './pagos/pagos.component';

const routes: Routes = [{
  path: '',
  component: PromotorComponent,
  children: [
    {
      path: 'precfdi',
      component: PreCfdiComponent,
    },
    {
      path: 'clientes',
      component: ClientesComponent,
    },
    {
      path: 'reportes',
      component: ReportesComponent,
    },
    {
      path: 'pagos',
      component: PagosComponent,
    }
  ]
}
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class PromotorRoutingModule { }