import UtilsBL from './utils.js'

export default {
  // Corrige ordenação de peças avulsas
  fixMovDoc: function (a) {
    var lastIdDocumento
    for (var i = 0; i < a.length; i++) {
      var movdoc = a[i]

      // verifica se a peça está fora de ordem
      if (movdoc.doc.idDocumento) {
        if (!lastIdDocumento === undefined) {
          lastIdDocumento = Number(movdoc.doc.idDocumento)
        } else if (Number(movdoc.doc.idDocumento) > lastIdDocumento) {
          // localizar o primeiro que já é menor do
          // que o que está fora de posição
          for (var j = 0; j < i; j++) {
            var md = a[j]
            if (
              md.doc.idDocumento &&
              Number(md.doc.idDocumento) < Number(movdoc.doc.idDocumento)
            ) {
              UtilsBL.arrayMove(a, i, j)
              break
            }
          }
        } else {
          lastIdDocumento = Number(movdoc.doc.idDocumento)
        }
      }
    }
  },
  filtrar: function (a, f) {
    try {
      var fs, i, k

      // Nenhum filtro
      if (!f || f.trim() === '') {
        fs = a
      } else if (f.substring(0, 1) === '#') {
        // Filtrador por hashtag
        var ff = f.split(' ')
        fs = []
        for (i = 0; i < a.length; i++) {
          for (k = 0; k < ff.length; k++) {
            if (
              (ff[k] === '#marca' && a[i].marca && a[i].marca.length > 0) ||
              (a[i].mov && a[i].mov.tipo && a[i].mov.tipo === ff[k])
            ) {
              fs.push(a[i])
              break
            }
          }
        }
      } else {
        fs = UtilsBL.filtrarPorSubstring(a, f)
      }

      a = fs

      // Quando existem duas ou mais linhas referentes ao
      // mesmo movimento, omitir o movimento e aumentar o
      // rowspan da primeira linha.
      for (i = 0; i < a.length - 1; i++) {
        if (a[i].mov) {
          a[i].hidemov = false
          a[i].rowspan = 1
        }
      }
      for (i = 0; i < a.length - 1; i++) {
        if (!a[i].mov || a[i].hidemov) {
          continue
        }
        for (k = i + 1; k < a.length; k++) {
          if (a[i].mov === a[k].mov) {
            a[i].rowspan++
            a[k].rowspan = 0
            a[k].hidemov = true
          } else {
            i = k - 1
            break
          }
        }
      }

      // Marcar pares e impares
      var odd = false
      for (i = 0; i < a.length; i++) {
        if (a[i].mov && !a[i].hidemov) {
          odd = !odd
        }
        a[i].odd = odd
      }
      return a
    } catch (ex) {
      return []
    }
  },
  mostrarTexto: function (movdoc, doc, f) {
    for (var i = 0; i < movdoc.length; i++) {
      var md = movdoc[i]
      if (doc === md.doc) {
        for (var j = i; j >= 0; j--) {
          if (movdoc[j].mov) {
            break
          }
        }
        if (
          movdoc[j].rowspan &&
          j < md.length - 1 &&
          movdoc[j + 1].rowspan === undefined
        ) {
          movdoc[j].rowspan += f ? 1 : -1
        }
        doc.exibirTexto = f
        break
      }
    }
  },
  colocarLink: function (s) {
    if (s) s = s.replace(' e ', ', ')
    var a = s.split(', ')
    for (var i = 0; i < a.length; i++) {
      a[i] =
        '<a href="#/processo/' +
        a[i] +
        '" target="_blank">' +
        this.formatarProcesso(a[i]) +
        '</a>'
    }
    return a.join(', ')
  },
  formatarTexto: function (s) {
    if (s === undefined) {
      return s
    }
    return s
      .replace(/^\s\s*/, '')
      .replace(/\s\s*$/, '')
      .replace(/\n\s+\n/g, '<div class="break"></div>')
      .replace(/\n/g, '<br/>')
  },
  somenteNumeros: function (s) {
    if (s === undefined) return
    return s.replace(/\D/g, '')
  },
  regexFormatarProcesso: /^(\d{7})-?(\d{2})\.?(\d{4})\.?(4)\.?(02)\.?(\d{4})\/?-?(\d{2})?/,
  formatarProcesso: function (filename) {
    var m = this.regexFormatarProcesso.exec(filename)
    if (!m) return
    var s =
      m[1] + '-' + m[2] + '.' + m[3] + '.' + m[4] + '.' + m[5] + '.' + m[6]
    if (m[7]) s += '/' + m[7]
    return s
  },
  arrayToString: function (a) {
    if (!Array.isArray(a)) return a
    var str = a.join(', ')
    var n = str.lastIndexOf(', ')
    if (n >= 0) {
      str = str.substring(0, n) + ' e ' + str.substring(n + 2)
    }
    return str
  },
  arrayOfStringsToObjects: function (a, props) {
    if (a === undefined) return
    if (!Array.isArray(a)) a = [a]
    var r = []
    for (var i = 0; i < a.length; i++) {
      var aa = a[i].split('|')
      var o = {}
      for (var j = 0; j < aa.length; j++) {
        o[props[j]] = aa[j]
      }
      r.push(o)
    }
    return r
  }
}
