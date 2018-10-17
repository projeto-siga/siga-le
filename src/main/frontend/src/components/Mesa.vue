<template>
  <div class="container-fluid content">
    <div class="row">
      <div class="col-md-12">
        <h4 class="text-center mt-3 mb-3">Mesa Virtual</h4>
      </div>
      <div class="col col-sm-12" v-if="errormsg">
        <p class="alert alert-danger">
          <strong>Erro!</strong> {{errormsg}}
        </p>
      </div>
    </div>

    <div class="row mb-3 d-print-none">
      <div class="col col-auto">
        <div class="input-group">
          <div class="input-group-addon">
            <span class="fa fa-search"></span>
          </div>
          <input type="text" class="form-control" placeholder="Filtrar" v-model="filtro" ng-model-options="{ debounce: 200 }">
        </div>
      </div>
      <div class="col col-auto ml-auto">
        <button type="button" @click="anotarEmLote()" class="btn btn-primary" title="">
          <span class="fa fa-sticky-note-o"></span> Anotar&nbsp;&nbsp;
          <span class="badge badge-pill badge-warning">{{filtradosEMarcados.length}}</span>
        </button>
        <button type="button" @click="assinarComSenhaEmLote()" class="btn btn-primary" title="">
          <span class="fa fa-shield"></span> Assinar&nbsp;&nbsp;
          <span class="badge badge-pill badge-warning">{{filtradosEMarcadosEAssinaveis.length}}</span>
        </button>
        <button type="button" @click="tramitarEmLote()" class="btn btn-primary" title="">
          <span class="fa fa-paper-plane-o"></span> Tramitar&nbsp;&nbsp;
          <span class="badge badge-pill badge-warning">{{filtradosEMarcadosETramitaveis.length}}</span>
        </button>
      </div>
    </div>

    <div class="row" v-if="filtrados.length == 0">
      <div class="col col-sm-12">
        <p class="alert alert-warning">
          <strong>Atenção!</strong> Nenhuma documento na mesa.
        </p>
      </div>
    </div>

    <div class="row" v-if="filtrados.length > 0">
      <div class="col-sm-12">
					<table class="table table-sm table-borderless">
            <tbody>
            <template v-for="f in filtrados">
							<tr v-if="f.grupoExibir" class="table-group">
								<th colspan="6" class="pt-5 pb-0 pl-0">
									<h4 class="mb-1">{{f.grupoNome}}</h4>
								</th>
							</tr>
							<tr v-if="f.grupoExibir" class="table-head">
              <th style="text-align: center">
                <input type="checkbox" id="progress_checkall" name="progress_checkall" v-model="todos" @change="marcarTodos()"></input>
              </th>
								<th>Tempo</th>
								<th>Código</th>
								<th>Descrição</th>
								<th>Origem</th>
								<th>Etiquetas</th>
							</tr>
							<tr v-bind:class="{odd: f.odd}">
              <td style="text-align: center">
                <input type="checkbox" v-model="f.checked" :disabled="f.disabled"></input>
              </td>
								<td :title="f.datahora">{{f.tempoRelativo}}</td>
								<td><router-link :to="{name: 'Documento', params: {numero: f.codigo}}">{{f.sigla}}</router-link></td>
								<td>{{f.descr}}</td>
								<td>{{f.origem}}</td>
								<td style="padding: 0;"><div  class="xrp-label-container">
									<!-- class="list-unstyled blog-tags" -->
										<span v-for="m in f.list" :title="m.titulo"><button
											class="btn btn-default btn-sm xrp-label"><i
												:class="'fa fa-' + m.icone"></i> {{m.nome}}<span
												v-if="m.pessoa &amp;&amp; !m.daPessoa"> -
													{{m.pessoa}}</span><span
												v-if="m.unidade &amp;&amp; (!m.daLotacao || (!m.daPessoa && !m.deOutraPessoa))">
													/ {{m.unidade}}</span></button></span></div>
								</td>
							</tr>
            </template>
            </tbody>
					</table>
      </div>
    </div>
  </div>
</template>

<script>
import { Bus } from '../bl/bus.js'
import UtilsBL from '../bl/utils.js'
// import { Bus } from '../bl/bus.js'

export default {
  components: {},

  mounted() {
    this.errormsg = undefined

    setTimeout(() => {
      this.carregarMesa()
    })
  },

  data() {
    return {
      mesa: undefined,
      filtro: undefined,
      lista: [],
      todos: true,
      errormsg: undefined
    }
  },

  computed: {
    filtrados: function() {
      var a = this.lista
      var grupo
      var odd = false
      a = UtilsBL.filtrarPorSubstring(a, this.filtro)
      a = a.filter(function(item) {
        return item.grupo !== 'NENHUM'
      })
      for (var i = 0; i < a.length; i++) {
        a[i].grupoExibir = a[i].grupo !== grupo
        grupo = a[i].grupo
        if (a[i].grupoExibir) odd = false
        odd = !odd
        a[i].odd = odd
      }
      return a
    },

    filtradosEMarcados: function() {
      return this.filtrados.filter(function(item) {
        return item.checked
      })
    },

    filtradosEMarcadosEAssinaveis: function() {
      return this.filtradosEMarcados.filter(function(item) {
        return item.grupo === 'A_ASSINAR'
      })
    },

    filtradosEMarcadosETramitaveis: function() {
      return this.filtradosEMarcados.filter(function(item) {
        return item.grupo === 'AGUARDANDO_ANDAMENTO'
      })
    }
  },

  methods: {
    carregarMesa: function() {
      this.$http.get('mesa', { block: true }).then(
        response => {
          this.lista.length = 0
          var list = response.data.list
          for (var i = 0; i < list.length; i++) {
            this.lista.push(this.fixItem(list[i]))
          }
        },
        error => UtilsBL.errormsg(error, this)
      )
    },

    fixItem: function(item) {
      UtilsBL.applyDefauts(item, {
        checked: true,
        disabled: false,
        grupo: undefined,
        grupoNome: undefined,
        grupoExibir: undefined,
        datahora: undefined,
        datahoraFormatada: undefined,
        sigla: undefined,
        codigo: undefined,
        descr: undefined,
        origem: undefined,
        situacao: undefined,
        errormsg: undefined,
        odd: undefined
      })
      if (item.datahora !== undefined) {
        item.datahoraFormatada = UtilsBL.formatJSDDMMYYYYHHMM(item.datahora)
      }
      return item
    },

    assinarComSenhaEmLote: function() {
      var a = this.filtradosEMarcadosEAssinaveis
      Bus.$emit('iniciarAssinaturaComSenha', a, this.carregarMesa)
    },

    anotarEmLote: function() {
      var a = this.filtradosEMarcados
      Bus.$emit('iniciarAnotacao', a)
    },

    tramitarEmLote: function() {
      var a = this.filtradosEMarcadosETramitaveis
      Bus.$emit('iniciarTramite', a, this.carregarMesa)
    },

    marcarTodos: function() {
      var docs = this.filtrados
      for (var i = 0; i < docs.length; i++) {
        var doc = docs[i]
        doc.checked = this.todos
      }
    },

    mostrarDocumento: function(item, disposition) {
      var form = document.createElement('form')
      form.action =
        this.$parent.test.properties['siga-le.assijus.endpoint'] +
        '/api/v1/view' +
        (disposition === 'attachment' ? '?disposition=attachment' : '')
      form.method = 'POST'
      form.target = '_blank'
      form.style.display = 'none'

      var cpf = document.createElement('input')
      cpf.type = 'text'
      cpf.name = 'cpf'
      cpf.value = this.$parent.jwt.cpf

      var system = document.createElement('input')
      system.type = 'text'
      system.name = 'system'
      system.value = item.docsystem

      var docid = document.createElement('input')
      docid.type = 'text'
      docid.name = 'id'
      docid.value = item.docid

      var docsecret = document.createElement('input')
      docsecret.type = 'text'
      docsecret.name = 'secret'
      docsecret.value = item.docsecret

      var submit = document.createElement('input')
      submit.type = 'submit'
      submit.id = 'submitView'

      form.appendChild(cpf)
      form.appendChild(system)
      form.appendChild(docid)
      form.appendChild(docsecret)
      form.appendChild(submit)
      document.body.appendChild(form)

      /* global $ */
      $('#submitView').click()

      document.body.removeChild(form)
    },

    criarAssinavel: function(item) {
      return {
        id: item.docid,
        system: item.docsystem,
        code: item.codigo,
        descr: item.docdescr,
        kind: item.dockind,
        origin: 'Balcão Virtual'
      }
    },

    assinarDocumento: function(item) {
      this.chamarAssijus([this.criarAssinavel(item)])
    },

    assinarDocumentos: function() {
      var list = []
      for (var i = 0; i < this.filtradosEMarcadosEAssinaveis.length; i++) {
        list.push(this.criarAssinavel(this.filtradosEMarcadosEAssinaveis[i]))
      }
      if (list.length > 0) this.chamarAssijus(list)
    },

    chamarAssijus: function(list) {
      var json = JSON.stringify({ list: list })
      this.$http
        .post(
          this.$parent.test.properties['siga-le.assijus.endpoint'] +
            '/api/v1/store',
          { payload: json },
          { block: true }
        )
        .then(
          response => {
            var callback = window.location.href + ''
            console.log(callback)
            window.location.href =
              this.$parent.test.properties['siga-le.assijus.endpoint'] +
              '/?endpointautostart=true&endpointlistkey=' +
              response.data.key +
              '&endpointcallback=' +
              encodeURI(callback).replace('#', '__hashsign__')
          },
          error => UtilsBL.errormsg(error, this)
        )
    },

    editar: function() {
      this.$refs.etiqueta.show()
    },

    exibirProcessosMultiplos: function() {
      this.$refs.processosMultiplos.show()
    },

    acrescentarProcessosNaLista: function(arr) {
      if (!arr || arr.length === 0) return
      this.pasta = 'inbox'
      for (var i = 0; i < arr.length; i++) {
        if (arr[i] === '') continue
        var p = this.fixProcesso({
          numero: arr[i],
          inbox: true
        })
        this.processos.push(p)
      }
      this.validarEmLoteSilenciosamente()
    }
  }
}
</script>

<style scoped>
.destaque {
  color: red;
}

.td-middle {
  vertical-align: middle;
}

table .table-group th {
  border-top: 0;
}

table .table-head th {
  border-top: 0;
}

.odd {
  background-color: rgba(0, 0, 0, 0.05);
}

.xrp-label-container {
  margin-top: 4px;
  margin-bottom: 0px;
}

.xrp-label {
  font-size: 13px;
  margin-bottom: 4px;
  margin-right: 8px;
  line-height: 1.1;
  padding-left: 7px;
  padding-right: 7px;
  border-radius: 0px;
  border: 1px solid #ccc;
}
</style>
