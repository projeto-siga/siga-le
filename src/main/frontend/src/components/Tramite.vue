<template>
  <div>
    <b-modal id="cota" ref="modal" v-model="showModal" title="Tramitar" hide-header-close>
      <form>
        <div class="row">
          <div class="form-group col col-sm-6">
            <label class="control-label" for="tipo" style="width: 100%">Destino</label>
             <select class="form-control" id="tipo" v-model="tipo">
                <option value="lotacao">Lotação</option>
                <option value="matricula">Pessoa</option>
              </select>
              <span v-if="false" v-show="errors.has('tipo')" class="help is-danger">{{ errors.first('tipo') }}</span>
          </div>
          <div class="form-group col col-sm-6" v-if="tipo === 'lotacao'">
            <label class="control-label" for="lotacao" style="width: 100%">Sigla da Lotação</label>
            <v-autocomplete :items="lotacoes" name="lotacao" id="lotacao" v-model="lotacao" :get-label="getLabelLotacao" :component-item='template' @update-items="updateLotacoes" input-class="form-control"></v-autocomplete>
            <span v-if="false" v-show="errors.has('lotacao')" class="help is-danger">{{ errors.first('lotacao') }}</span>
          </div>
          <div class="form-group col col-sm-6" v-if="tipo === 'matricula'">
            <label class="control-label" for="matricula" style="width: 100%">Matrícula</label>
            <v-autocomplete :items="pessoas" name="matricula" id="matricula" v-model="matricula" :get-label="getLabelPessoa" :component-item='template' @update-items="updatePessoas" input-class="form-control"></v-autocomplete>
            <span v-if="false" v-show="errors.has('matricula')" class="help is-danger">{{ errors.first('matricula') }}</span>
          </div>
        </div>
        <em v-if="errormsg &amp;&amp; errormsg !== ''" for="processos" class="invalid">{{errormsg}}</em>
      </form>
      <div style="width: 100%" slot="modal-footer">
        <b-btn class="float-right ml-2" variant="primary" @click="save" :disabled="errors.any()">
          Prosseguir
        </b-btn>
        <b-btn class="float-right" variant="secondary" @click="$refs.modal.hide(false)">
          Cancelar
        </b-btn>
      </div>
    </b-modal>
  </div>
</template>

<script>
import { Bus } from '../bl/bus.js'
import ItemTemplate from './ItemTemplate.vue'
import UtilsBL from '../bl/utils.js'

export default {
  name: 'tramite',

  mounted () {
  },

  data () {
    return {
      showModal: false,
      errormsg: undefined,
      tipo: 'lotacao',
      lotacao: null,
      matricula: null,
      documentos: undefined,
      item: 'Monica',
      lotacoes: [],
      pessoas: [],
      template: ItemTemplate
    }
  },

  methods: {
    getLabelLotacao: function (item) {
      return item
    },
    getLabelPessoa: function (item) {
      return item
    },
    updatePessoas: function (text) {
      // yourGetItemsMethod(text).then((response) => {
      //   this.items = response
      // })
      if (!text || text === '') return
      this.errormsg = undefined
      this.$http.get('pessoa/' + encodeURI(text) + '/pesquisar').then(
        response => {
          this.pessoas = []
          var l = response.data.list
          if (l) {
            for (var i = 0; i < l.length; i++) {
              this.pessoas.push(l[i].sigla + ' - ' + l[i].nome)
            }
          }
        },
        error => {
          UtilsBL.errormsg(error, this)
        }
      )
    },
    updateLotacoes: function (text) {
      // yourGetItemsMethod(text).then((response) => {
      //   this.items = response
      // })
      if (!text || text === '') return
      this.errormsg = undefined
      this.$http.get('lotacao/' + encodeURI(text) + '/pesquisar').then(
        response => {
          this.lotacoes = []
          var l = response.data.list
          if (l) {
            for (var i = 0; i < l.length; i++) {
              this.lotacoes.push(l[i].sigla + ' - ' + l[i].nome)
            }
          }
        },
        error => {
          UtilsBL.errormsg(error, this)
        }
      )
    },

    show: function (documentos, cont) {
      this.showModal = true
      this.errormsg = undefined
      this.documentos = documentos
      this.matricula = null
      this.lotacao = null
      this.cont = cont
    },

    cancel: function (e) {
      e.cancel()
    },

    save: function () {
      if (this.tipo === 'lotacao') {
        this.matricula = undefined
        if ((this.lotacao || '') === '') {
          this.errormsg = 'Lotação deve ser informada.'
          return
        }
      }
      if (this.tipo === 'matricula') {
        this.lotacao = undefined
        if ((this.matricula || '') === '') {
          this.errormsg = 'Matrícula deve ser informada.'
          return
        }
      }

      var lotacao = this.lotacao
      if (lotacao) lotacao = lotacao.split(' - ')[0]
      var matricula = this.matricula
      if (matricula) matricula = matricula.split(' - ')[0]

      Bus.$emit('tramitar', this.documentos, lotacao, matricula, this.cont)
      this.$refs.modal.hide(true)
    },

    validar: function () {
      this.$nextTick(() => this.$validator.validateAll())
    }
  },

  components: {
//    'pdf-preview': PdfPreview
  }
}
</script>
