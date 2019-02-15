<template>
  <div>
    <b-modal id="cota" ref="modal" v-model="showModal" title="Anotar" hide-header-close>
      <form>
        <div class="row">
          <div class="form-group col col-sm-12">
            <label class="control-label" for="anotacao" style="width: 100%">Texto da Anotação</label>
            <textarea name="anotacao" id="anotacao" v-model="anotacao" maxlength="255" class="form-control" aria-describedby="anotacaoHelp" :class="{'is-invalid': errors.has('anotacao') }" style="width: 100%" :rows="3" autofocus></textarea>
              <span v-if="false" v-show="errors.has('anotacao')" class="help is-danger">{{ errors.first('anotacao') }}</span>
          </div>
        </div>
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

export default {
  name: 'anotacao',

  mounted () {
  },

  data () {
    return {
      showModal: false,
      errormsg: undefined,
      documentos: undefined,
      anotacao: undefined
    }
  },

  methods: {
    show: function (documentos, cont) {
      this.showModal = true
      this.errormsg = undefined
      this.documentos = documentos
      this.anotacao = undefined
      this.cont = cont
    },

    cancel: function (e) {
      e.cancel()
    },

    save: function () {
      if ((this.anotacao || '') === '') {
        this.errormsg = 'Texto da anotação deve ser informado.'
        return
      }
      Bus.$emit('anotar', this.documentos, this.anotacao, this.cont)
      this.$refs.modal.hide(true)
    },

    validar: function () {
      this.$nextTick(() => this.$validator.validateAll())
    }
  },

  components: {
  }
}
</script>
